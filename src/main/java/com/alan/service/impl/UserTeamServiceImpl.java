package com.alan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alan.pojo.domain.UserTeam;
import com.alan.service.UserTeamService;
import com.alan.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Alan
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-08-18 11:20:09
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




