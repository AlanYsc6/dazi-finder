package com.alan.pojo.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户队伍关系
 *
 * @TableName user_team
 */
@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = -7223945988377137642L;

    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;
    /**
     * 队伍创建人
     */
    private UserVO createUser;
    /**
     * 队伍成员列表
     */
    private List<UserVO> userList;

}