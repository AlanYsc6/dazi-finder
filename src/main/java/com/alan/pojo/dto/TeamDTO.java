package com.alan.pojo.dto;

import com.alan.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍查询信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamDTO extends PageRequest implements Serializable {

    private static final long serialVersionUID = -4215933883278353668L;

    /**
     * id
     */
    private Long id;
    /**
     * idList
     */
    private List<Long> idList;
    /**
     * 查询文本信息
     */
    private String SearchText;
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
     * 密码
     */
    private String password;

}