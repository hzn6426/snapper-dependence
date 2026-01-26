/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomibing.web.common;

import com.baomibing.tool.util.Checker;
import com.baomibing.web.annotation.NotWrap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;


/**
 * 封装统一返回结果，仅对本项目<tt>com.canaan</tt>包下的方法进行返回结果封装，避免影响swagger相关请求及其他插件的请求
 * <ul>
 * <li>void类型会统一封装成{@link R}</li>
 * <li>ModelAndView会原样返回</li>
 * <li>{@link R}类型会进行判断是否抛出异常，如果抛出异常并且是<tt>Ajax</tt>请求，封装ModelAndView返回错误页面，否则原样返回</li>
 * <li>其他类型封装成{@link R}</li>
 * </ul>
 * 
 * @author zening
 * @since 1.0.0
 */
@RestControllerAdvice
public class ReturnHandlerAdvice implements ResponseBodyAdvice<Object> {

	@Value("${snapper.wrap.package}") private String wrapPackage;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		boolean bePrivate = Modifier.PRIVATE == returnType.getMethod().getModifiers();
		//private  方法直接跳过
		if (bePrivate) {
			return false;
		}

		NotWrap notWrap = returnType.getMethod().getAnnotation(NotWrap.class);
		if (notWrap != null) {
			return false;
		}
//		Class<?> clazz = returnType.getMethod().getReturnType();
//		String clazzName = clazz.getName();
//		boolean beByteArrayClass = "[B".equals(clazzName);
//		if (beByteArrayClass) {
//			return false;
//		}
		String name = returnType.getMethod().getDeclaringClass().getName();
		//仅对本项目结果进行封装
		return name.startsWith("com.baomibing") || (Checker.beNotEmpty(wrapPackage) && name.startsWith(wrapPackage));
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		String url = request.getURI().getPath();
		if (url.startsWith("/fapi/")) {
			return body;
		}
		//封装返回结果
		if (body == null) {
			return R.build().withRequest(url);
		}
		
		if (ModelAndView.class.isInstance(body)) {
			ModelAndView mv = (ModelAndView) body;
//			Object v =  mv.getModel().get(SystemConstants.ERROR_ATTRIBUTE);
//			if (v != null) {
//				return (ResponseResult<?>) v;
//			}
			return mv;
		}
		
		if (R.class.isInstance(body)) {
			R<?> r = (R<?>) body;
			return r.withRequest(url);
		}
		
		if (Collection.class.isInstance(body)) {
			Collection<?> ction = (Collection<?>) body;
			List<?> list = null;
			if (ction instanceof List) {
				list = (List<?>) ction;
			} else {
				list = Lists.newArrayList(ction);
			}
			return R.build(list).withRequest(url);
		}
		//TODO this will fix the StringHttpMessageConvert for R can not cast to Primitive type.
//		Class< ? > clazz = returnType.getMethod().getReturnType();
//		if (Checker.BeNotNull(clazz) && (ClassUtils.isPrimitiveOrWrapper(clazz) || String.class == clazz)) {
//			Map<String, Object> json = new HashMap<>();
//			json.put("key", body);
//			return R.build(Lists.newArrayList(json));
//		}
		
		return R.build(Lists.newArrayList(body)).withRequest(url);
	}

}
