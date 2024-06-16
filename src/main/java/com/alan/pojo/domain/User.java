package com.alan.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户主键id，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    private String username;

    /**
     * 用户名
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码，非空
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 星球编号
     */
    private String planetCode;
    /**
     * 标签
     */
    private String tags;
    /**
     * 用户状态，默认0：正常
     */
    private Integer userStatus;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除，默认0：否
     */
    @TableLogic
    private Integer isDelete;
    /**
     * 用户角色，默认0：普通用户,1管理员
     */
    private Integer userRole;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}