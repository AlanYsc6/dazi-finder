package com.alan.exception;

import com.alan.common.BaseResponse;
import com.alan.common.ErrorCode;
import com.alan.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author Alan
 * @Date 2024/5/17 13:43
 * @Description 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GloableExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse busiessExceptionHandler(BusinessException e){
        log.info("业务异常{}",e.getMessage());
        return ResultUtils.error(e.getCode(),e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptinHandler(RuntimeException e){
        log.info("运行时异常{}",e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
