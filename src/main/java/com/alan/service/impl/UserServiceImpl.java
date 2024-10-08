package com.alan.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alan.common.ErrorCode;
import com.alan.exception.BusinessException;
import com.alan.mapper.UserMapper;
import com.alan.pojo.domain.User;
import com.alan.pojo.vo.UserVO;
import com.alan.service.UserService;
import com.alan.utils.AlgorithmUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.alan.constant.UserConstant.USER_ROLE_ADMIN;
import static com.alan.constant.UserConstant.USER_STATE_LOGIN;

/**
 * @author Alan
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-05-13 21:27:27
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    private static final String SALT = "ysc";

    /**
     * 用户注册
     *
     * @param userAccount   用户名
     * @param userPassword  密码
     * @param checkPassword 确认密码
     * @param planetCode    星球编号
     * @return 用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {

        //判断输入内容非空
        boolean blank = StrUtil.hasBlank(userAccount, userPassword, checkPassword, planetCode);

        if (blank) {
            log.info("用户注册失败，输入内容为空");
            throw new BusinessException(ErrorCode.PARAM_NULL, "参数为空");
        }
        //判断用户名合法--数字，字母，下划线，不小于四位。
        boolean userAccountGeneral = Validator.isGeneral(userAccount, 4);
        if (!userAccountGeneral) {
            log.info("用户注册失败，用户名不合法");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名不合法");
        }
        //判断密码合法--数字，字母，下划线，不小于六位。
        boolean passwordGeneral = Validator.isGeneral(userPassword, 6);
        if (!passwordGeneral) {
            log.info("用户注册失败，密码不合法");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码不合法");
        }
        //判断星球编号合法--数字，不大于5位。
        if (!(Validator.isNumber(planetCode) && Validator.isGeneral(planetCode, 2, 5))) {
            log.info("用户注册失败，星球编号不合法");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "星球编号不合法");
        }
        //判断二次密码是否一致
        if (!userPassword.equals(checkPassword)) {
            log.info("用户注册失败，二次密码不一致");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "二次密码不一致");
        }
        //判断用户名是否重复
        boolean userAccountRepeat = this.count(new LambdaQueryWrapper<User>().eq(User::getUserAccount, userAccount)) > 0;
        if (userAccountRepeat) {
            log.info("用户注册失败，用户名已存在");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名已存在");
        }
        //判断星球编号是否重复
        boolean planetCodeRepeat = this.count(new LambdaQueryWrapper<User>().eq(User::getPlanetCode, planetCode)) > 0;
        if (planetCodeRepeat) {
            log.info("用户注册失败，星球编号已存在");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "星球编号已存在");
        }
        //密码加密
        String encryptPsw = SecureUtil.md5(SALT + userPassword);
        //插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPsw);
        user.setPlanetCode(planetCode);
        boolean result = this.save(user);
        if (result) {
            log.info("用户注册成功");
            return user.getId();
        } else {
            log.info("用户注册失败");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户注册失败");
        }
    }

    //获取近十天日期
    public List<LocalDate> getDateList() {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate end = LocalDate.now();
        LocalDate begin = end.minusDays(10);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }

    /**
     * 根据标签查询用户
     *
     * @return
     */
    @Override
    public List<UserVO> searUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        List<UserVO> userVOList = new ArrayList<>();
        for (User user : userList) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVOList.add(userVO);
        }
        return userVOList;
    }

    /**
     * 更新用户
     *
     * @param user      用户信息
     * @param loginUser 请求信息
     * @return 更新状态
     */
    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户id不合法");
        }
        //管理员可以更新任意用户
        //非管理员只能更新自己
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        return userMapper.updateById(user);
    }

    /**
     * 用户鉴权
     *
     * @param request 请求信息
     * @return 鉴权结果
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_STATE_LOGIN);
        if (userObj == null) {
            log.info("user is not login");
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        UserVO userVO = (UserVO) userObj;
        return Objects.equals(userVO.getUserRole(), USER_ROLE_ADMIN);
    }

    /**
     * 用户鉴权
     *
     * @param loginUser 当前用户
     * @return 鉴权结果
     */
    @Override
    public boolean isAdmin(User loginUser) {
        if (loginUser == null) {
            log.info("loginUser is null");
            throw new BusinessException(ErrorCode.PARAM_NULL, "用户未登录");
        }
        return Objects.equals(loginUser.getUserRole(), USER_ROLE_ADMIN);
    }

    @Override
    public List<UserVO> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
            .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
            .limit(num)
            .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<UserVO>> userIdUserListMap = this.list(userQueryWrapper)
            .stream()
            .map(user -> {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                return userVO;
            })
            .collect(Collectors.groupingBy(UserVO::getId));
        List<UserVO> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }


    /**
     * 获取登录用户
     *
     * @param request 请求信息
     * @return 登录用户
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_STATE_LOGIN);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return (User) userObj;
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户名
     * @param userPassword 密码
     * @param request      请求信息
     * @return 用户信息(脱敏)
     */
    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //判断输入内容非空
        boolean blank = StrUtil.hasBlank(userAccount, userPassword);

        if (blank) {
            log.info("用户登录失败，输入内容为空");
            return null;
        }
        //判断用户名合法--数字，字母，下划线，不小于四位。
        boolean userAccountGeneral = Validator.isGeneral(userAccount, 4);
        if (!userAccountGeneral) {
            log.info("用户登录失败，用户名不合法");
            return null;
        }
        //判断密码合法--数字，字母，下划线，不小于六位。
        boolean passwordGeneral = Validator.isGeneral(userPassword, 6);
        if (!passwordGeneral) {
            log.info("用户登录失败，密码不合法");
            return null;
        }
        //密码加密
        String encryptPsw = SecureUtil.md5(SALT + userPassword);
        //查询数据库
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUserAccount, userAccount).eq(User::getUserPassword, encryptPsw));
        if (user == null) {
            log.info("用户登录失败，用户名或密码错误");
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        request.getSession().setAttribute(USER_STATE_LOGIN, userVO);
        log.info("用户登录成功");
        return userVO;
    }

    /**
     * 用户注销
     *
     * @param request 请求信息
     * @return 注销结果
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_STATE_LOGIN);
        return 1;
    }


}




