package com.alan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alan.pojo.domain.Team;
import com.alan.service.TeamService;
import com.alan.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Alan
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-08-18 11:20:06
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




