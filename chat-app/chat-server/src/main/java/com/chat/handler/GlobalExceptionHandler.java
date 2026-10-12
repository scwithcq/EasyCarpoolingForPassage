package com.chat.handler;

import com.chat.constant.MessageConstant;
import com.chat.exception.BaseException;
import com.chat.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，统一把异常转换为 Result 返回，避免向前端暴露堆栈
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BaseException.class)
    public Result exceptionHandler(BaseException ex) {
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result exceptionHandler(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldError() == null
                ? MessageConstant.UNKNOWN_ERROR
                : ex.getBindingResult().getFieldError().getDefaultMessage();
        log.error("参数校验失败：{}", msg);
        return Result.error(msg);
    }

    /**
     * 数据库唯一约束冲突（mybatis-spring 通常包装为 DataIntegrityViolationException）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result exceptionHandler(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        log.error("SQL约束异常：{}", message);
        if (message != null && message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            return Result.error(split[2] + MessageConstant.ALREADY_EXISTS);
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /**
     * 请求体解析失败（如时间格式不匹配、字段类型错误）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result exceptionHandler(HttpMessageNotReadableException ex) {
        log.error("请求参数格式错误：{}", ex.getMessage());
        return Result.error(MessageConstant.PARAM_FORMAT_ERROR);
    }

    /**
     * 数据库唯一约束冲突
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        log.error("SQL约束异常：{}", message);
        if (message != null && message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            return Result.error(split[2] + MessageConstant.ALREADY_EXISTS);
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception ex) {
        log.error("系统异常：", ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}