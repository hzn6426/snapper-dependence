/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询，封装分页条件及对象查询条件
 * 
 * @param <E>
 * @author zening
 * @since 1.0.0
 */
@Data @Accessors(chain = true)
public class PageQuery<E> implements Serializable {
	private static final long serialVersionUID = -134459139009770321L;
	
	/**
	 * should be the dto object
	 */
	private E dto ;
	
	private int pageNo = 0;
	
	private int pageSize = 20;
	
	private List<String> ascs;
	
	private List<String> descs;
	
}
