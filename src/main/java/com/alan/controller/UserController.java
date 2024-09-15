package com.alan.controller;

import cn.hutool.core.util.StrUtil;
import com.alan.common.BaseResponse;
import com.alan.common.ErrorCode;
import com.alan.exception.BusinessException;
import com.alan.pojo.domain.User;
import com.alan.pojo.dto.UserLoginDTO;
import com.alan.pojo.dto.UserRegisterDTO;
import com.alan.pojo.vo.RegdVO;
import com.alan.pojo.vo.UserVO;
import com.alan.service.UserService;
import com.alan.utils.AliOssUtil;
import com.alan.utils.ResultUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.alan.constant.UserConstant.USER_STATE_LOGIN;

/**
 * @Author Alan
 * @Date 2024/5/14 22:04
 * @Description 针对表【user(用户表)】的controller
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Autowired
    private AliOssUtil aliOssUtil;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    //OSS文件上传
    @PostMapping("/upload")
    public BaseResponse<String> upload(MultipartFile file) {
        log.info("OSS文件上传:{}", file);
        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构建新文件名称
            String objectName = aliOssUtil.getFolderName() + UUID.randomUUID().toString() + extension;
            //文件请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return ResultUtils.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败:{}", e);
        }
        return ResultUtils.error("文件上传失败");
    }

    @GetMapping("/regd")
    public List<RegdVO> getRegd() {
        List<RegdVO> regdVOList = new ArrayList<>();
        //获取从begin到end的日期
        List<LocalDate> dateList = userService.getDateList();
        for (LocalDate dateT : dateList) {
            //查询dateT这天，create_time=dateT的用户数
            LocalDateTime beginTime = LocalDateTime.of(dateT, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(dateT, LocalTime.MAX);
            Integer regs = Math.toIntExact(userService.count(new LambdaQueryWrapper<User>().between(User::getCreateTime, beginTime, endTime)));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            String date = dateT.format(formatter);
            regdVOList.add(new RegdVO(date, regs));
        }
        return regdVOList;
    }

    /**
     * 用户注册
     *
     * @param userRegisterDTO 注册信息
     * @return 注册成功后返回用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("userRegisterDTO: {}", userRegisterDTO);
        if (userRegisterDTO == null) {
            log.info("userRegisterDTO is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "注册信息不能为空");
        }

        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        String planetCode = userRegisterDTO.getPlanetCode();

        boolean blank = StrUtil.hasBlank(userAccount, userPassword, checkPassword, planetCode);

        if (blank) {
            log.info("userAccount or userPassword or checkPassword or planetCode is blank");
            throw new BusinessException(ErrorCode.PARAM_NULL, "注册信息不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginDTO 登录信息
     * @param request      请求
     * @return 登录成功后返回用户信息
     */

    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        log.info("userLoginDTO: {}", userLoginDTO);
        if (userLoginDTO == null) {
            log.info("userLoginDTO is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "登录信息不能为空");
        }

        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();

        boolean blank = StrUtil.hasBlank(userAccount, userPassword);

        if (blank) {
            log.info("userAccount or userPassword is blank");
            throw new BusinessException(ErrorCode.PARAM_NULL, "登录信息不能为空");
        }

        UserVO userVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request 请求信息
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        if (request.getSession() == null) {
            log.info("session is null");
            throw new BusinessException(ErrorCode.PARAM_NULL, "session is null");
        }
        Object userObj = request.getSession().getAttribute(USER_STATE_LOGIN);
        if (userObj == null) {
            log.info("user is not login");
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        UserVO userVO = (UserVO) userObj;
        long userVOId = userVO.getId();
        User user = userService.getById(userVOId);
        if (user == null) {
            log.info("user is null");
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户不存在");
        }
        BeanUtils.copyProperties(user, userVO);
        log.info("userVO: {}", userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 用户注销
     *
     * @param request 请求信息
     * @return 注销结果
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request.getSession() == null) {
            log.info("session is null");
            throw new BusinessException(ErrorCode.PARAM_NULL, "session is null");
        }

        int logout = userService.userLogout(request);
        return ResultUtils.success(logout);
    }

    /**
     * 查询用户
     *
     * @param username 昵称
     * @param request  请求信息
     * @return 用户集合
     */
    @GetMapping("/search")
    public BaseResponse<List<UserVO>> userSearchByName(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            log.info("user is not admin");
            throw new BusinessException(ErrorCode.NO_AUTH, "用户无权限");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        boolean blank = StrUtil.hasBlank(username);
        if (!blank) {
            queryWrapper.like(User::getUsername, username);
        }
        List<User> users = userService.list(queryWrapper);
        List<UserVO> userVos = new ArrayList<>();
        users.forEach(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVos.add(userVO);
        });
        log.info("query succeeded");
        return ResultUtils.success(userVos);
    }

    /**
     * 根据标签查询用户
     *
     * @param tagNameList 标签列表
     * @return 用户列表
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<UserVO>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAM_NULL);
        }
        List<UserVO> userList = userService.searUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 删除用户
     *
     * @param id      用户id
     * @param request 请求信息
     * @return 删除结果
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> userDeleteById(@RequestBody Long id, HttpServletRequest request) {
        log.info("id: {}", id);
        if (id <= 0) {
            log.info("id is invalid");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户id不合法");
        }
        if (!userService.isAdmin(request)) {
            log.info("user is not admin");
            throw new BusinessException(ErrorCode.NO_AUTH, "用户无权限");
        }
        boolean remove = userService.removeById(id);
        return ResultUtils.success(remove);
    }

    /**
     * 修改用户
     *
     * @param user 待修改用户信息
     * @return 修改结果
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(User user, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Integer result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 推荐用户
     *
     * @param request 请求信息
     * @return 用户集合
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<UserVO>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("dazi:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        //如果有缓存，直接读缓存
        Page<UserVO> userVOPageCache = (Page<UserVO>) valueOperations.get(redisKey);
        if (userVOPageCache != null) {
            return ResultUtils.success(userVOPageCache);
        }
        //无缓存，查数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        Page<User> users = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
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
        try {
            valueOperations.set(redisKey, userVoPage, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return ResultUtils.success(userVoPage);
    }

    /**
     * 获取最匹配的用户
     *
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<UserVO>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, user));
    }

}