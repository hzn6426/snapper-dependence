/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * bean工具类，提供设置 获取属性等相关的简洁方法
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class BeanUtil {

	/**
	 * 获取对象的属性名称，以属性名<code>propertyNames</code>列表为key,具体值为value，返回map，如果propertyNames不指定则默认为对象所有属性。
	 * 
	 * @param bean          操作的对象
	 * @param propertyNames 属性名称
	 * @return 属性名和值对应的map
	 */
	public static Map<String, Object> describe(Object bean, String... propertyNames) {
		Validate.notNull(bean, "bean can't be null!");
		if (!Checker.beNotEmpty(propertyNames)) {
			try {
				Field[] fields = FieldUtils.getAllFields(bean.getClass());
				Map<String, Object> map = new LinkedHashMap<String, Object>(fields.length);
				for (Field field : fields) {
					map.put(field.getName(), field.get(bean));
				}
				return map;
			} catch (Exception e) {
				throw new RuntimeException("describe exception", e);
			}
		}
		Map<String, Object> map = new LinkedHashMap<>(propertyNames.length);
		for (String propertyName : propertyNames) {
			map.put(propertyName, getProperty(bean, propertyName));
		}
		return map;
	}

	/**
	 * 获取对象的属性<code>propertyName</code>对应的值
	 * 
	 * @param <T>          对象属性值对应的类型
	 * @param bean         操作的对象
	 * @param propertyName 属性名称
	 * @return 获取的对象值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getProperty(Object bean, String propertyName) {
		Validate.notNull(bean, "bean can't be null!");
		Validate.notBlank(propertyName, "propertyName can't be blank!");
		try {
			Field field = FieldUtils.getField(bean.getClass(), propertyName, true);
			return (T) field.get(bean);
//            return (T) PropertyUtils.getProperty(bean, propertyName);
		} catch (Exception e) {
			throw new RuntimeException("getProperty exception", e);
		}
	}

	/**
	 * 设置对象对应的属性<code>propertyName</code>名称对应的值为<code>value</code>，操作当value不为null有效.
	 * 
	 * @param bean         操作的对象
	 * @param propertyName 属性名称
	 * @param value        设置的属性值
	 */
	public static void setPropertyIfValueNotNull(Object bean, String propertyName, Object value) {
		if (null != value) {
			setProperty(bean, propertyName, value);
		}
	}

	/**
	 * 设置对象对应的属性<code>propertyName</code>名称对应的值为<code>value</code>.
	 * 
	 * @param bean         操作的对象
	 * @param propertyName 属性名称
	 * @param value        设置的属性值
	 */
	public static void setProperty(Object bean, String propertyName, Object value) {
		Validate.notNull(bean, "bean can't be null!");
		Validate.notBlank(propertyName, "propertyName can't be null!");
		try {
			// for lombok dynamic setter getter , must use Introspection.
			// PropertyUtils.setProperty(bean, propertyName, value); will be wrong.
			Field field = FieldUtils.getField(bean.getClass(), propertyName, true);
			field.set(bean, value);
//          PropertyUtils.setProperty(bean, propertyName, value);
		} catch (Exception e) {
			throw new RuntimeException("setProperty exception" + e);
		}
	}

	/**
	 * 判断bean属性是否存在
	 * 
	 * @param bean         操作对象
	 * @param propertyName 属性名称
	 * @return 是否有属性
	 */
	public static boolean hasPrperty(Object bean, String propertyName) {
		return FieldUtils.getField(bean.getClass(), propertyName, true) != null;
	}

}
