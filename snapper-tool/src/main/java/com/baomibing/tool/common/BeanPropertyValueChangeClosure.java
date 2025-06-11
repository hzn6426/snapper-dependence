/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.common;


import com.baomibing.tool.util.BeanUtil;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.Validate;


/**
 * 对象属性修改闭包
 * <p>
 * 一个对象针对一条属性，可以设置断言@see {@link Predicate}，当断言成立时，属性修改
 * </p>
 * 
 * @param <T>
 * @author zening
 * @since 1.0.0
 */
public class BeanPropertyValueChangeClosure<T> implements Closure<T> {

	private final String propertyName;
	
	private final Object propertyValue;
	
	private final Predicate<T> predicate;
	
	public BeanPropertyValueChangeClosure(String propertyName, Object propertyValue){
        Validate.notBlank(propertyName, "propertyName can't be blank!");
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.predicate = null;
    }
	
	public BeanPropertyValueChangeClosure(Predicate<T> predicate, String propertyName, Object propertyValue){
        Validate.notBlank(propertyName, "propertyName can't be blank!");
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.predicate = predicate;
    } 
	
	@Override
	public void execute(T input) {
		if (null == input) {
			return;
		}
		if (predicate != null && !predicate.evaluate(input)) return;
		BeanUtil.setProperty(input, propertyName, propertyValue);
		
	}

}
