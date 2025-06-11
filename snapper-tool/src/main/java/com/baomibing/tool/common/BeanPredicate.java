/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
