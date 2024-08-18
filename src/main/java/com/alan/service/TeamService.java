package com.alan.service;

import com.alan.pojo.domain.Team;
import com.alan.pojo.domain.User;
import com.alan.pojo.dto.TeamDTO;
import com.alan.pojo.vo.TeamUserVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @author Alan
 * @description 针对表【team(队伍)】的数据库操作Service
 * @createDate 2024-08-18 11:04:21
 */
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamDTO teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamDTO teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamDTO teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamDTO teamQuitRequest, User loginUser);


    /**
     * 删除（解散）队伍
     *
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);

}
