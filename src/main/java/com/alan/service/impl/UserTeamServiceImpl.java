package com.alan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alan.pojo.domain.UserTeam;
import com.alan.service.UserTeamService;
import com.alan.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alan
 * @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
 * @createDate 2024-08-18 11:20:09
 */
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
        implements UserTeamService {

    @Override
    public List<Long> getIdListByUserId(Long id) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", id);
        List<UserTeam> userTeamList = this.list(queryWrapper);
        return new ArrayList<>(userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId)).keySet());
    }

}




