/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.common;


import com.alibaba.fastjson.JSON;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.util.Checker;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Ajax请求返回结果，所有的Ajax请求都返回该对象的反序列化串，包括发生异常，
 * 
 * @param <E>
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Getter 
public class R<E> {
	/**
	 * 业务编码
	 */
	private int code;
	/**
	 * 业务消息
	 */
	private String message;
	/**
	 * 业务数据
	 */
	private List<E> data;
	/**
	 * 数据总条数
	 */
	private Integer totalCount;
	/**
	 * 当前页面编号
	 */
	private Integer pageNo;
	/**
	 * 当前页面数据条数
	 */
	private Integer pageSize;
	/**
	 * 数据总页数
	 */
	private Integer totalPage;
	/**
	 * 当前请求路径
	 */
	private String path;//请求路径
	
	private R(int code, String message, int pageNo, int pageSize, int totalSize, List<E> data) {
		this.code = code;
		this.message = message;
		this.pageNo = pageNo;
		this.totalCount = totalSize;
		this.data = data;
		this.pageSize = pageSize;
		this.totalPage = totalCount == 0 ? 0 : totalCount / pageSize + (totalSize % pageSize == 0 ? 0 : 1);
	}
	
	private R(int code, String message, Integer totalSize, List<E> data) {
		this.code = code;
		this.message = message;
		this.totalCount = totalSize;
		this.data = data;
	}
	
	private R(List<E> data) {

		this.code = 200;
		this.message = "OK";
		this.data = data;
	}
	
	public static <E> R<E> build() {
		return new R<E>(200, "OK", 1, 10, 0, Lists.newArrayList());
	}
	
	public static <E> R<E> build(int code, String message) {
		return new R<E>(code, message, null, null);
	}
	
	public static <E> R<E> build(List<E> data) {
		return new R<E>(data);
	}
	
	public static <E> R<E> build(SearchResult<E> result) {
		if (result == null) {
			return build(200, Lists.newArrayList());
		}
		return build(result.getTotalSize(), result.getDataList());
	}
	
	public static <E> R<E> build(int totalSize, List<E> data) {
		return new R<E>(200, "OK", totalSize, data);
	}
	
	public static <E> R<E> build(int totalSize, int pageNo, int pageSize, List<E> data) {
		return new R<E>(200, "OK", pageNo, pageSize, totalSize, data);
	}
	
	public static <E> R<E> build(int code, String message, int pageNo, int pageSize, int totalSize, List<E> data) {
		return new R<E>(code, message, pageNo, pageSize, totalSize, data);
	}
	
	
	public static <E> R<E> build(ServerRuntimeException bizException) {
		return bizException == null ? build() : build(bizException.getCode(), bizException.getMessage());
	}

	public R<E> withRequest(String path) {
		this.path = path;
		return this;
	}

	public R<E> withRequest(HttpServletRequest request) {
		if (Checker.beNotNull(request)) {
			this.path = request.getRequestURI();
		}
		return this;
	}
	
	public  String json() {
		return JSON.toJSONString(this);
	}
	
}
