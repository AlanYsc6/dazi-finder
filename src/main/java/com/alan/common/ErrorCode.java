package com.alan.common;

/**
 * @Author Alan
 * @Date 2024/5/17 12:47
 * @Description 全局错误码
 */
public enum ErrorCode {
    SUCCESS(0, "ok", ""),
    PARAM_ERROR(40000, "请求参数错误", ""),
    PARAM_NULL(40001, "请求参数为空", ""),
    NULL_ERROR(40010, "对象为空错误", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "权限不足", ""),
    SYSTEM_ERROR(50000, "系统内部异常", ""),;
    /**
     * 错误码
     */
    private final int code;
    /**
     * 错误信息
     */
    private final String message;
    /**
     * 错误描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
