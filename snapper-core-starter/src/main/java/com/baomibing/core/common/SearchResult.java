/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
