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
