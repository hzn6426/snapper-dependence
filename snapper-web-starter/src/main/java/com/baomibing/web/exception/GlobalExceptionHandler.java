/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.exception;


import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.util.Checker;
import com.baomibing.web.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j @RestControllerAdvice
public class GlobalExceptionHandler extends ExceptionHandlerExceptionResolver {

	@ExceptionHandler(ServerRuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> bizException(ServerRuntimeException ex, HttpServletRequest request) {
        log.error("ServerRunTimeException:", ex);
        return R.build(ex.getCode(), ex.getMessage()).withRequest(request);
    }
	
	@ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<?> notFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        log.error("NoHandlerFoundException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.NO_HANDER_FOUND_EXCEPTION)).withRequest(request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> httpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("HttpMessageNotReadableException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.MESSAGE_NOT_READABLE_ERROR)).withRequest(request);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> bindException(BindException ex, HttpServletRequest request) {
        log.error("BindException:", ex);
        try {
            String vmessage = ex.getBindingResult().getFieldError().getDefaultMessage();
			if (Checker.beNotEmpty(vmessage)) {
                return R.build(new ServerRuntimeException(ExceptionEnum.BIND_ARGUMENT_VALIDATE_ERROR, vmessage)).withRequest(request);
            }
        } catch (Exception ee) {
        }
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = ex.getFieldErrors();
        fieldErrors.forEach((oe) ->
                msg.append("参数:[").append(oe.getObjectName())
                        .append(".").append(oe.getField())
                        .append("]的传入值:[").append(oe.getRejectedValue()).append("]与预期的字段类型不匹配.")
        );
        return R.build(new ServerRuntimeException(ExceptionEnum.BIND_ARGUMENT_VALIDATE_ERROR, msg)).withRequest(request);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("MethodArgumentTypeMismatchException:", ex);
        MethodArgumentTypeMismatchException eee = ex;
        StringBuilder msg = new StringBuilder("参数：[").append(eee.getName())
                .append("]的传入值：[").append(eee.getValue())
                .append("]与预期的字段类型：[").append(eee.getRequiredType().getName()).append("]不匹配");
        return R.build(new ServerRuntimeException(ExceptionEnum.BIND_ARGUMENT_VALIDATE_ERROR, msg)).withRequest(request);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> illegalStateException(IllegalStateException ex, HttpServletRequest request) {
        log.error("IllegalStateException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.INVALID_ARGUMENTS_EXCEPTION)).withRequest(request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> missingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.error("MissingServletRequestParameterException:", ex);
        StringBuilder msg = new StringBuilder();
        msg.append("缺少必须的[").append(ex.getParameterType()).append("]类型的参数[").append(ex.getParameterName()).append("]");
        return R.build(new ServerRuntimeException(ExceptionEnum.MISSING_SERVLET_REQUEST_PARAM,msg)).withRequest(request);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> nullPointerException(NullPointerException ex, HttpServletRequest request) {
        log.error("NullPointerException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.OBJECT_IS_NULL)).withRequest(request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> illegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("IllegalArgumentException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.INVALID_ARGUMENTS_EXCEPTION)).withRequest(request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.error("HttpMediaTypeNotSupportedException:", ex);
        MediaType contentType = ex.getContentType();
        if (contentType != null) {
            return R.build(new ServerRuntimeException(ExceptionEnum.REQUEST_CONTENT_TYPE_NOT_MATCH, contentType.toString())).withRequest(request);
        }
        return R.build(new ServerRuntimeException(ExceptionEnum.INVALID_REQUEST_CONTENT_TYPE)).withRequest(request);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> missingServletRequestPartException(MissingServletRequestPartException ex, HttpServletRequest request) {
        log.error("MissingServletRequestPartException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.REQUEST_FILE_PARAM_EXCEPTION)).withRequest(request);
    }

    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> servletException(ServletException ex, HttpServletRequest request) {
        log.error("ServletException:", ex);
        String msg = "UT010016: Not a multi part request";
        if (msg.equalsIgnoreCase(ex.getMessage())) {
        	return R.build(new ServerRuntimeException(ExceptionEnum.REQUEST_FILE_PARAM_EXCEPTION)).withRequest(request);
        }
        return R.build(new ServerRuntimeException(ExceptionEnum.SYSTEM_IS_BUSY)).withRequest(request);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> multipartException(MultipartException ex, HttpServletRequest request) {
        log.error("MultipartException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.REQUEST_FILE_PARAM_EXCEPTION)).withRequest(request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> constraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        log.error("ConstraintViolationException:", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return R.build(new ServerRuntimeException(ExceptionEnum.BASE_PARAM_VALID_EXCEPTION, message)).withRequest(request);
    }

    /**
     * spring 封装的参数验证异常， 在conttoller中没有写result参数时，会进入
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.BASE_PARAM_VALID_EXCEPTION, ex.getBindingResult().getFieldError().getDefaultMessage())).withRequest(request);
    }

    /**
     * 其他异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> otherExceptionHandler(Exception ex, HttpServletRequest request) {
        log.error("Exception:", ex);
        if (ex.getCause() instanceof ServerRuntimeException) {
            return this.bizException((ServerRuntimeException) ex.getCause(), request);
        }
        return R.build(new ServerRuntimeException(ExceptionEnum.SYSTEM_IS_BUSY)).withRequest(request);
    }


    //返回状态码:405
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("HttpRequestMethodNotSupportedException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.REQUEST_METHOD_NOT_SUPPORT)).withRequest(request);
    }


//    @ExceptionHandler(PersistenceException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> persistenceException(PersistenceException ex, HttpServletRequest request) {
//        log.error("PersistenceException:", ex);
//        if (ex.getCause() instanceof ServerRuntimeException) {
//            return this.bizException((ServerRuntimeException) ex.getCause(), request);
//        }
//        return R.build(new ServerRuntimeException(ExceptionEnum.SQL_EXECUTE_EXCEPTION)).withRequest(request);
//    }
//
//    @ExceptionHandler(MyBatisSystemException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> myBatisSystemException(MyBatisSystemException ex, HttpServletRequest request) {
//        log.error("PersistenceException:", ex);
//        if (ex.getCause() instanceof PersistenceException) {
//            return this.persistenceException((PersistenceException) ex.getCause(), request);
//        }
//        return R.build(new ServerRuntimeException(ExceptionEnum.SQL_EXECUTE_EXCEPTION)).withRequest(request);
//    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> sqlException(SQLException ex, HttpServletRequest request) {
        log.error("SQLException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.SQL_EXECUTE_EXCEPTION)).withRequest(request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> dataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("DataIntegrityViolationException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.SQL_EXECUTE_EXCEPTION)).withRequest(request);
    }
}
