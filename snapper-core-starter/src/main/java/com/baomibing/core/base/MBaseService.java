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
package com.baomibing.core.base;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


/**
 * 通用服务封装
 * 
 * @param <E>
 * @author zening
 * @since 1.0.0
 */
public interface MBaseService<E> {

	E getIt(Serializable pk);

	E getItCache(Serializable pk);

	List<E> gets(Set<? extends Serializable> idSet);

	void saveIt(E e);

	void saveItBatch(List<E> elist);

	void updateIt(E e);

	void updateItBatch(List<E> elist);

	void saveOrUpdate(E e);

	void saveOrUpdateBatch(List<E> elist);

	void deleteIt(E e);

	void deleteItBatch(List<E> elist);

	void deletes(Set<? extends Serializable> ids);

}
