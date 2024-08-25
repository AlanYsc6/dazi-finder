package com.alan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.alan.pojo.domain.UserTeam;
import java.util.List;

/**
* @author Alan
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service
* @createDate 2024-08-18 11:07:15
*/
public interface UserTeamService extends IService<UserTeam> {

    /**
     * 根据用户id获取其所属的队伍id列表
     * @param id 用户id
     * @return 队伍id列表
     */
    List<Long> getIdListByUserId(Long id);

}
