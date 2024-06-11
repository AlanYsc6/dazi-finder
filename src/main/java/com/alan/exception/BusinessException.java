package com.alan.exception;

import com.alan.common.ErrorCode;

/**
 * @Author Alan
 * @Date 2024/5/17 13:12
 * @Description 全局异常处理类
 */
public class BusinessException extends RuntimeException{

    private static final long serialVersionUID = 92797686650012857L;

    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description){
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode,String description){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
