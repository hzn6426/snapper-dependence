/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.security.exception;

import com.baomibing.authority.exception.*;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.util.Checker;
import com.baomibing.web.common.R;
import com.baomibing.web.exception.NotRedirectFromGateWayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class AuthorityWebExceptionHandler {

	@ExceptionHandler(ServerRuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> bizException(ServerRuntimeException ex, HttpServletRequest request) {
        log.error("ServerRunTimeException:", ex);
        return R.build(ex.getCode(), ex.getMessage()).withRequest(request);
    }
	
	@ExceptionHandler(NotRedirectFromGateWayException.class)//不经过gateway直接请求
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> notFoundException(NotRedirectFromGateWayException ex, HttpServletRequest request) {
        log.error("NotRedirectFromGateWayException:", ex);
        return R.build(new ServerRuntimeException(ExceptionEnum.NO_PRIVILEGE_EXCEPTION)).withRequest(request);
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
    
//    //所有的feignException都会被封装成HystrixRuntimeException
//    @ExceptionHandler(HystrixRuntimeException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R<?> hystrixRuntimeException(HystrixRuntimeException ex, HttpServletRequest request) {
//        log.error("HystrixRuntimeException:", ex);
//        if (ex.getCause() instanceof FeignRequestException) {
//        	FeignRequestException fr = (FeignRequestException) ex.getCause();
//        	return R.build(new ServerRuntimeException(fr.getCode(), fr.getCodeMessage())).withRequest(request);
//        }
//        return otherExceptionHandler(ex, request);
//    }

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
		ex.printStackTrace();
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


    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> persistenceException(PersistenceException ex, HttpServletRequest request) {
        log.error("PersistenceException:", ex);
        if (ex.getCause() instanceof ServerRuntimeException) {
            return this.bizException((ServerRuntimeException) ex.getCause(), request);
        }
        return R.build(new ServerRuntimeException(ExceptionEnum.SQL_EXECUTE_EXCEPTION)).withRequest(request);
    }

    @ExceptionHandler(MyBatisSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> myBatisSystemException(MyBatisSystemException ex, HttpServletRequest request) {
        log.error("PersistenceException:", ex);
        if (ex.getCause() instanceof PersistenceException) {
            return this.persistenceException((PersistenceException) ex.getCause(), request);
        }
        return R.build(new ServerRuntimeException(ExceptionEnum.SQL_EXECUTE_EXCEPTION)).withRequest(request);
    }

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
    
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> userNameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
    	 log.error("UsernameNotFoundException:", ex);
    	 return R.build(new ServerRuntimeException(ExceptionEnum.USER_NAME_NOT_EXIST)).withRequest(request);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> badCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
    	 log.error("BadCredentialsException:", ex);
    	 return R.build(new ServerRuntimeException(ExceptionEnum.USER_NAME_OR_PASSWD_NOT_CORRECT)).withRequest(request);
    }

    @ExceptionHandler(TokenBeKickedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> tokenBeKickedException(TokenBeKickedException ex, HttpServletRequest request) {
        log.error("TokenBeKickedException:", ex);
        return R.build(new ServerRuntimeException(AuthorizationExceptionEnum.USER_LOGIN_IN_ANOTHER_PLACE)).withRequest(request);
    }

    @ExceptionHandler(DecodeRASKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> badCredentialsException(DecodeRASKeyException ex, HttpServletRequest request) {
        log.error("BadCredentialsException:", ex);
        return R.build(new ServerRuntimeException(AuthorizationExceptionEnum.DECODE_RSA_EXCEPTION)).withRequest(request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> invalidTokenException(InvalidTokenException ex, HttpServletRequest request) {
        log.error("InvalidTokenException:", ex);
        return R.build(new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_TOKEN_EXCEPTION)).withRequest(request);
    }

    @ExceptionHandler(TokenTimeOutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> tokenTimeOutException(TokenTimeOutException ex, HttpServletRequest request) {
        log.error("TokenTimeOutException:", ex);
        return R.build(new ServerRuntimeException(AuthorizationExceptionEnum.TOKEN_TIME_OUT_EXCEPTION)).withRequest(request);
    }


    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> internalAuthenticationServiceException(InternalAuthenticationServiceException ex, HttpServletRequest request) {
        log.error("InternalAuthenticationServiceException:", ex);
        if (ex.getCause() instanceof UserNotActiveException) {
            return R.build(new ServerRuntimeException(ExceptionEnum.USER_NOT_ACTIVE)).withRequest(request);
        } else if (ex.getCause() instanceof UserStoppedException) {
            return R.build(new ServerRuntimeException(ExceptionEnum.USER_ACCOUNT_STOPPED)).withRequest(request);
        } else if (ex.getCause() instanceof UserLockedException) {
            return R.build(new ServerRuntimeException(ExceptionEnum.USER_ACCOUNT_LOCKED)).withRequest(request);
        } else if (ex.getCause() instanceof AuthPassExpiredException) {
            return R.build(new ServerRuntimeException(ExceptionEnum.USER_AUTH_CODE_EXPIRE)).withRequest(request);
        }   else if (ex.getCause() instanceof NotSupportPointException) {
            return R.build(new ServerRuntimeException(ExceptionEnum.USER_POINT_NOT_SUPPORT)).withRequest(request);
        }
        return R.build(new ServerRuntimeException(ExceptionEnum.USER_NAME_OR_PASSWD_NOT_CORRECT)).withRequest(request);
    }
    
}
