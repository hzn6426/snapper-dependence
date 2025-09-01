/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.coder;

import com.alibaba.fastjson.JSONObject;
import com.baomibing.feign.exception.FeignRequestException;
import com.baomibing.tool.constant.Strings;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

/**
 * feign错误解码处理
 * 
 * @author zening
 * @since 1.0.0
 */
public class FeignErrorDecoder implements ErrorDecoder {
	
	private final ErrorDecoder defaultErrorDecoder = new Default();
	@Override
    public Exception decode(String methodKey, Response response) {
		try {
			String bodyString = StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8);
			if (bodyString.contains(Strings.CODE) && bodyString.contains(Strings.MESSAGE)) {
				JSONObject json = JSONObject.parseObject(bodyString);
				int code = json.getIntValue(Strings.CODE);
				String message = json.getString(Strings.MESSAGE);
				if (code != 200) {
					return new FeignRequestException(code, message);
				}
			}
		} catch (Exception e) {
			return e;
		}
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
