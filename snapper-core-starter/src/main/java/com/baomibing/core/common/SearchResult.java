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
package com.baomibing.core.common;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 服务通用查询结果
 * 
 * @param <T>
 * @author zening
 * @since 1.0.0
 */
@Setter @Getter
public class SearchResult <T> implements Serializable{
	private static final long serialVersionUID = -6073327088728107693L;
	private int totalSize;
	private List<T> dataList;
	
	public SearchResult() {
		this.dataList = Lists.newArrayList();
		this.totalSize = 0;
	}
	
	public SearchResult(int totalSize, List<T> dataList) {
		this.totalSize = totalSize;
		this.dataList = dataList;
		
	}
	
}
