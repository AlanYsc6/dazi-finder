package com.alan.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Alan
 * @Date 2024/5/14 21:45
 * @Description 登录dto
 */
@Data
public class UserRegisterDTO implements Serializable {
    private static final long serialVersionUID = -954285071833996638L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;
}
