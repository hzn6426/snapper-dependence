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

import com.baomibing.tool.util.BeanUtil;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.Validate;



/**
 * 用于对象属性值预言的对象
 * 
 * @author zening
 * @since 1.0.0
 */
public class BeanPredicate<T> implements Predicate<T>{
	
	private final String propertyName;

    @SuppressWarnings("rawtypes")
	private final Predicate valuePredicate;
    
    @SuppressWarnings("rawtypes")
	public BeanPredicate(String propertyName, Predicate valuePredicate){
        Validate.notBlank(propertyName, "propertyName can't be blank!");
        Validate.notNull(valuePredicate, "predicate can't be null!");
        this.propertyName = propertyName;
        this.valuePredicate = valuePredicate;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public boolean evaluate(T object){
        Object currentPropertyValue = BeanUtil.getProperty(object, propertyName);
        return valuePredicate.evaluate(currentPropertyValue);
    }
}
