/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import com.baomibing.tool.common.BeanPredicate;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Equator;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.functors.ComparatorPredicate;
import org.apache.commons.collections4.functors.ComparatorPredicate.Criterion;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

/**
 * 断言对象相关工具类
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class BeanPredicateUtil {
	/**
	 * 生成与对象的属性名称<code>propertyName</code>和属性值<code>propertyValue</code>匹配的断言
	 * 
	 * @param <T>           断言类型
	 * @param <V>           属性值的对象类型
	 * @param propertyName  属性名称
	 * @param propertyValue 属性值
	 * @return 属性名称与属性值匹配的断言
	 */
	public static <T, V> Predicate<T> equalPredicate(String propertyName, V propertyValue) {
		Validate.notBlank(propertyName, "propertyName can't be blank!");
		return new BeanPredicate<>(propertyName, PredicateUtils.equalPredicate(propertyValue));
	}

	/**
	 * 生成与对象的属性名称<code>propertyName</code>和属性值<code>propertyValue</code>匹配的断言，并忽略大小写
	 * 
	 * @param <T>           断言类型
	 * @param propertyName  属性名称
	 * @param propertyValue 属性值
	 * @return 属性名称与属性值匹配的忽略大小写断言
	 */
	public static <T> Predicate<T> equalIgnoreCasePredicate(String propertyName, String propertyValue) {
		Validate.notBlank(propertyName, "propertyName can't be blank!");
		return new BeanPredicate<>(propertyName, EqualPredicate.equalPredicate(propertyValue, new Equator<String>() {

			@Override
			public boolean equate(String o1, String o2) {
				return StringUtils.equalsIgnoreCase(o1, o2);
			}

			@Override
			public int hash(String o) {
				return o == null ? -1 : o.hashCode();
			}

		}));
	}

	/**
	 * 生成多个以map方式封装的多个属性属性与值匹配的断言
	 * <p>
	 * <b>场景:</b> 在list中查找 名字是 关羽,并且 年龄是30 的user
	 * </p>
	 * 
	 * <pre class="code">
	 * 
	 * User guanyu30 = new User("关羽", 30);
	 * List{@code <User>} list = toList(
	 * 	new User("张飞", 23),
	 *  new User("关羽", 24),
	 *  new User("刘备", 25),
	 *  guanyu30);
	 * 
	 * Predicate{@code <User>} predicate = PredicateUtils
	 *  .andPredicate(BeanPredicateUtil.equalPredicate("name", "关羽"), BeanPredicateUtil.equalPredicate("age", 30));
	 * 
	 * assertEquals(guanyu30, LoopUtil.find(list, predicate));
	 * </pre>
	 * 
	 * <p>
	 * 此时你可以优化成:
	 * </p>
	 * 
	 * <pre class="code">
	 * 
	 * User guanyu30 = new User("关羽", 30);
	 * List{@code <User>} list = toList(
	 * 	new User("张飞", 23),
	 *  new User("关羽", 24),
	 *  new User("刘备", 25),
	 *  guanyu30);
	 * 
	 * Map{@code <String, Object>} map = ConvertUtil.toMap("name", "关羽", "age", 30);
	 * assertEquals(guanyu30, find(list, BeanPredicateUtil.{@code <User>} equalPredicate(map)));
	 * </pre>
	 * 
	 * @param <T>                             断言的类型
	 * @param propertyNameAndPropertyValueMap 属性值匹配map
	 * @return 生成的断言
	 */
	public static <T> Predicate<T> equalPredicate(Map<String, ?> propertyNameAndPropertyValueMap) {
		Validate.notEmpty(propertyNameAndPropertyValueMap, "propertyNameAndPropertyValueMap can't be null!");

		@SuppressWarnings("unchecked")
		BeanPredicate<T>[] beanPredicates = (BeanPredicate<T>[]) Array.newInstance(BeanPredicate.class, propertyNameAndPropertyValueMap.size());

		int index = 0;
		for (Map.Entry<String, ?> entry : propertyNameAndPropertyValueMap.entrySet()) {
			String propertyName = entry.getKey();
			Object propertyValue = entry.getValue();

			Validate.notBlank(propertyName, "propertyName can't be blank!");

			Array.set(beanPredicates, index, equalPredicate(propertyName, propertyValue));
			index++;
		}
		return PredicateUtils.allPredicate(beanPredicates);
	}

	/**
	 * 根据对象属性列表及对应的值匹配的断言
	 * 
	 * @param bean          操作的对象
	 * @param propertyNames 属性名称
	 * @return 生成的匹配断言
	 */
	public static <T> Predicate<T> equalPredicate(T bean, String... propertyNames) {
		if (null == bean) {
			return PredicateUtils.nullPredicate();
		}
		Map<String, ?> propertyNameAndPropertyValueMap = BeanUtil.describe(bean, propertyNames);
		return equalPredicate(propertyNameAndPropertyValueMap);
	}

	/**
	 * 根据对象属性名称<code>propertyName</code>生成对应的值包含在<code>propertyValueList</code>中的断言
	 * 
	 * @param <T>               对象的类型
	 * @param <V>               属性值类型
	 * @param propertyName      属性名称
	 * @param propertyValueList 属性值列表
	 * @return 生成的断言
	 */
	public static <T, V> Predicate<T> containsPredicate(final String propertyName, final Collection<V> propertyValueList) {
		Validate.notBlank(propertyName, "propertyName can't be blank!");
		return new BeanPredicate<>(propertyName, new Predicate<V>() {

			@Override
			public boolean evaluate(V propertyValue) {
				return null == propertyValue ? false : propertyValueList.contains(propertyValue);
			}
		});
	}

	/**
	 * 根据对象属性名称<code>propertyName</code>生成对应的值包含在<code>propertyValueList</code>中的断言
	 * 
	 * @param <T>            对象的类型
	 * @param <V>            属性值类型
	 * @param propertyName   属性名称
	 * @param propertyValues 属性值列表
	 * @return 生成的断言
	 */
	@SafeVarargs
	public static <T, V> Predicate<T> containsPredicate(final String propertyName, final V... propertyValues) {
		Validate.notBlank(propertyName, "propertyName can't be blank!");
		return new BeanPredicate<>(propertyName, new Predicate<V>() {

			@Override
			public boolean evaluate(V propertyValue) {
				return org.apache.commons.lang3.ArrayUtils.contains(propertyValues, propertyValue);
			}
		});
	}

	/**
	 * 根据属性名称<code>propertyName</code>及属性值<code>valueToCompare</code>生成基于匹配<code>criterion</code>符号的断言
	 * <p>
	 * <b>场景:</b> 小于20岁的人分个组
	 * </p>
	 * 
	 * <pre class="code">
	 * List{@code <User>} list = toList(//
	 *                 new User("张飞", 10),
	 *                 new User("张飞", 28),
	 *                 new User("刘备", 32),
	 *                 new User("刘备", 30),
	 *                 new User("刘备", 10));
	 * Predicate{@code <User>} comparatorPredicate = BeanPredicateUtil.comparatorPredicate("age", 20, Criterion.LESS);
	 * List{@code <User>} list = LoopUtil.select(list, comparatorPredicate);
	 * 
	 * </pre>
	 * 
	 * @param <T>            对象的类型
	 * @param <V>            属性值类型
	 * @param propertyName   属性名称
	 * @param valueToCompare 匹配的属性值
	 * @param criterion      匹配符号
	 * @return 生成的匹配断言
	 */
	public static <T, V extends Comparable<? super V>> Predicate<T> comparatorPredicate(String propertyName, V valueToCompare, Criterion criterion) {
		Validate.notBlank(propertyName, "propertyName can't be blank!");
		return comparatorPredicate(propertyName, valueToCompare, ComparatorUtils.<V>naturalComparator(), criterion);
	}

	/**
	 * 根据属性名称<code>propertyName</code>及属性值<code>valueToCompare</code>生成基于比较器<code>comparator</code>进行比较匹配<code>criterion</code>符号的断言
	 * 
	 * @param <T>            对象的类型
	 * @param <V>            属性值类型
	 * @param propertyName   属性名称
	 * @param valueToCompare 匹配的属性值
	 * @param criterion      匹配符号
	 * @return 生成的匹配断言
	 */
	public static <T, V extends Comparable<? super V>> Predicate<T> comparatorPredicate(String propertyName, V valueToCompare, Comparator<V> comparator, Criterion criterion) {
		Validate.notBlank(propertyName, "propertyName can't be blank!");
		return new BeanPredicate<>(propertyName, new ComparatorPredicate<V>(valueToCompare, comparator, criterion));
	}
}
