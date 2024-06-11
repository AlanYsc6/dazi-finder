package com.alan.utils;

import com.alan.common.BaseResponse;
import com.alan.common.ErrorCode;

/**
 * @Author Alan
 * @Date 2024/5/17 11:09
 * @Description 统一返回结果集
 */
public class ResultUtils {
    /**
     * 成功
     * @param data 返回数据
     * @param <T> 泛型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     * @param errorCode 错误码
     * @return BaseResponse
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }
    /**
     * 失败
     * @return BaseResponse
     */
    public static BaseResponse error(String message) {
        return new BaseResponse<>(message);
    }

    /**
     * 失败
     * @param errorCode 错误码
     * @return BaseResponse
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse(errorCode.getCode(), null, message, description);
    }

    /**
     * 失败
     * @param errorCode 错误码
     * @return BaseResponse
     */
    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse(errorCode.getCode(), errorCode.getMessage(), description);
    }


    /**
     * 失败
     * @param code 错误码
     * @return BaseResponse
     */
    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, null, message, description);
    }

}
