/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.exception;

import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;
@Slf4j
public class GlobalErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resourceProperties, ErrorProperties errorProperties,
										  ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }
    
    

    @Override
	protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
    	Throwable error = super.getError(request);
    	log.error(error.getMessage());
    	error.printStackTrace();
    	Map<String, Object> map = super.getErrorAttributes(request, options);
    	
    	if (error instanceof DecodeRASKeyException) {
    		map = new GateWayRuntimeException(GateWayExceptionEnum.DECODE_RSA_EXCEPTION).toMap();
    	} else if (error instanceof InvalidTokenException) {
    		map = new GateWayRuntimeException(GateWayExceptionEnum.INVALID_TOKEN_EXCEPTION).toMap();
    	} else if (error instanceof TokenTimeOutException) {
    		map = new GateWayRuntimeException(GateWayExceptionEnum.TOKEN_TIME_OUT_EXCEPTION).toMap();
    	} else if (error instanceof UNClearUserGroupException) {
    		map = new GateWayRuntimeException(GateWayExceptionEnum.UNCLEAR_USER_GROUP_EXCEPTION).toMap();
    	} else if (error instanceof TokenBeKickedException) {
    		map = new GateWayRuntimeException(GateWayExceptionEnum.USER_LOGIN_IN_ANOTHER_PLACE).toMap();
    	} else if (error instanceof GateWayRuntimeException) {
    		map = ((GateWayRuntimeException) error).toMap();
    	} else if (error instanceof Exception){
    		map = new GateWayRuntimeException(GateWayExceptionEnum.SYSTEM_IS_BUSY).toMap();
    	} 
        return map;
	}



	@Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

	
    protected Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {

        final Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        Object ocode = errorPropertiesMap.get(Strings.CODE);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        int code = Checker.beEmpty(ObjectUtil.toStringIfNotNull(ocode)) ? -1 : Integer.valueOf(ocode.toString());
        switch (code) {
		case 401:
			status = HttpStatus.UNAUTHORIZED;
			break;
		case 403:
			status = HttpStatus.FORBIDDEN;
			break;
		default:
			break;
		}
        return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(errorPropertiesMap));//invoke mvc handler 
    }

}
