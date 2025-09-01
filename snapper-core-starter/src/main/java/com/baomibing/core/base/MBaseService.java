/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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

	E getItCahe(Serializable pk);

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
