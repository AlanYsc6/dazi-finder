package com.alan.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Alan
 * @Date 2024/5/14 21:45
 * @Description 登录dto
 */
@Data
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 7554985458726364504L;

    private String userAccount;

    private String userPassword;

}
