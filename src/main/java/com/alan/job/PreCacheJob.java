package com.alan.job;

import com.alan.pojo.domain.User;
import com.alan.pojo.vo.UserVO;
import com.alan.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author Alan
 * @Date 2024/7/3 16:13
 * @Description
 */
@Slf4j
@Component
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final List<Long> mainUserIds = Arrays.asList(1L, 2L, 3L);

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void preCache() {
        RLock lock = redissonClient.getLock("dazi:preCacheJob:preCache:lock");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {//看门狗机制
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                Page<User> users = userService.page(new Page<>(1, 8), queryWrapper);
                List<UserVO> userVos = users.getRecords().stream().map(user -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(user, userVO);
                    return userVO;
                }).collect(Collectors.toList());
                log.info("query succeeded");
                Page<UserVO> userVoPage = new Page<>();
                userVoPage.setRecords(userVos);
                userVoPage.setTotal(users.getTotal());
                userVoPage.setSize(users.getSize());
                userVoPage.setPages(users.getPages());
                userVoPage.setCurrent(users.getCurrent());
                for (Long mainUserId : mainUserIds) {
                    // 预缓存逻辑
                    String redisKey = String.format("dazi:user:recommend:%s", mainUserId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(redisKey, userVoPage, 1, TimeUnit.DAYS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("preCache error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
