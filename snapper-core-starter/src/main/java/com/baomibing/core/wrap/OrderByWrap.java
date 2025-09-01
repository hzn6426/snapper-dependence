/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.wrap;

import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * 自定义排序封装
 * 
 * @author zening
 * @since 1.0.0
 */
public class OrderByWrap {
	
	@Getter
	private final List<String> ascList = Lists.newArrayList(); 
	
	@Getter
	private final List<String> descList = Lists.newArrayList();
	
	public  OrderByWrap asc(String... fields) {
		if (ArrayUtils.isNotEmpty(fields)) {
			ascList.addAll(Lists.newArrayList(fields));
		}
		return this;
	}
	
	public OrderByWrap desc(String... fields) {
		if (ArrayUtils.isNotEmpty(fields)) {
			descList.addAll(Lists.newArrayList(fields));
		}
		return this;
	}
	
	public  OrderByWrap asc(List<String> fields) {
		if (Checker.beNotEmpty(fields)) {
			ascList.addAll(fields);
		}
		return this;
	}
	
	public  OrderByWrap desc(List<String> fields) {
		if (Checker.beNotEmpty(fields)) {
			descList.addAll(fields);
		}
		return this;
	}
	
	
	
}
