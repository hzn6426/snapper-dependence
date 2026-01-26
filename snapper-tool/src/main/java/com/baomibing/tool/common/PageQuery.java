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
