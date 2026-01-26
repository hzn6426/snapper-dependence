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
package com.baomibing.web.base;

import com.baomibing.core.base.MBaseService;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * 基于mybatis plus的通用controller
 * <p>
 * 其中baseService是根据类型进行匹配，例如继承该controller中的<code>E</code>为UserDto,
 * 则service会匹配继承自{@link MBaseService}且泛型UserDto的service
 * </p>
 * 
 * @param <E>
 * @author zening
 * @since 1.0.0
 */
public class MBaseController<E> extends ActionController{
	@Autowired private MBaseService<E> baseService;

	
	protected E getIt(Serializable id) {
		return baseService.getIt(id);
	}
	
	protected void saveIt(E e) {
		baseService.saveIt(e);
	}
	
	protected void updateIt(E e) {
		baseService.updateIt(e);
	}
	
	protected void deleteIt(E e) {
		baseService.deleteIt(e);
	}
	
	protected void deleteByBatch(List<? extends Serializable> ids) {
		baseService.deletes(Sets.newHashSet(ids));
	}
}
