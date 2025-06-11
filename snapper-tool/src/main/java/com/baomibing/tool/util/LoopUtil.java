/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import com.baomibing.tool.common.BeanPredicate;
import com.baomibing.tool.common.BeanPropertyValueChangeClosure;
import org.apache.commons.collections4.*;
import org.apache.commons.collections4.functors.ChainedClosure;
import org.apache.commons.collections4.functors.WhileClosure;
import org.apache.commons.lang3.Validate;

import java.util.*;

import static java.util.Collections.emptyMap;

/**
 * 循环相关工具类
 * <ol>
 * <li>修改集合中对象属性的方法</li>
 * <li>查询集合中对象属性值的方法</li>
 * <li>抽取集合中对象属性的方法</li>
 * <li>分组集合中对象属性的方法</li>
 * </ol>
 *
 * @author zening
 * @since 1.0.0
 */
public abstract class LoopUtil {

	/**
	 * 执行循环体操作闭包操作
	 *
	 * @param <I>      循环体中对象的类型
	 * @param iterable 循环体对象
	 * @param closure  闭包对象
	 */
	public static <I> void doLoop(final Iterable<I> iterable, final Closure<? super I> closure) {
		IterableUtils.forEach(iterable, closure);
	}

	/**
	 * 将循环体中属性为 <code>propertyName</code> 对应的值修改为<code>propertyValue</code>
	 *
	 * @param <I>           循环体中对象类型
	 * @param beanIterable  循环体
	 * @param propertyName  属性名称
	 * @param propertyValue 属性值
	 * @return 修改成功为true,否则为false
	 */
	public static <I> boolean changePropertyValue(final Iterable<I> beanIterable, String propertyName,
												  Object propertyValue) {
		if (Checker.beNotEmpty(beanIterable)) {
			IterableUtils.forEach(beanIterable, new BeanPropertyValueChangeClosure<I>(propertyName, propertyValue));
			return true;
		}
		return false;
	}

	/**
	 * 将循环体中属性为 <code>propertyName</code>
	 * 对应的值在<code>predicate</code>成立的情况下修改为<code>propertyValue</code>
	 *
	 * @param <I>           循环体中对象类型
	 * @param beanIterable  循环体
	 * @param predicate     预言条件
	 * @param propertyName  属性名称
	 * @param propertyValue 属性值
	 * @return 修改成功为true,否则为false
	 */
	public static <I> boolean changePropertyValue(final Iterable<I> beanIterable, Predicate<I> predicate,
												  String propertyName, Object propertyValue) {
		if (Checker.beNotEmpty(beanIterable)) {
			BeanPropertyValueChangeClosure<I> changeClosure = new BeanPropertyValueChangeClosure<I>(propertyName,
					propertyValue);
			WhileClosure<I> whileClosure = (WhileClosure<I>) WhileClosure.whileClosure(predicate, changeClosure, false);
			IterableUtils.forEach(beanIterable, whileClosure);

		}
		return false;
	}

	/**
	 * 将循环体中的对象满足<code>predicate</code>条件时，对多个属性进行修改，按照<code>propertyValueMap<属性名,属性值></code>方式修改
	 *
	 * @param <I>              循环体中对象类型
	 * @param beanIterable     循环体
	 * @param predicate        预言条件
	 * @param propertyValueMap 属性，属性值映射表
	 * @return 修改成功为true,否则为false
	 */
	public static <I> boolean changePropertyValue(final Iterable<I> beanIterable, Predicate<I> predicate,
												  Map<String, Object> propertyValueMap) {
		if (Checker.beNotEmpty(beanIterable) && Checker.beNotEmpty(propertyValueMap)) {
			MapIterator<String, Object> mapIter = MapUtils.iterableMap(propertyValueMap).mapIterator();
			List<BeanPropertyValueChangeClosure<I>> changedList = new LinkedList<>();
			while (mapIter.hasNext()) {
				changedList.add(new BeanPropertyValueChangeClosure<I>(mapIter.next(), mapIter.getValue()));
			}
			ChainedClosure<I> chainClosure = (ChainedClosure<I>) ChainedClosure.chainedClosure(changedList);
			WhileClosure<I> whileClosure = (WhileClosure<I>) WhileClosure.whileClosure(predicate, chainClosure, false);
			IterableUtils.forEach(beanIterable, whileClosure);
			return true;
		}
		return false;
	}

	/**
	 * 将循环体中的对象按照闭包的规则进行修改
	 *
	 * @param <I>            循环体中对象类型
	 * @param beanIterable   循环体
	 * @param changeClosures 属性修改闭包
	 * @return 修改成功为true,否则为false
	 */
	@SafeVarargs
	public static <I> boolean changePropertyValue(final Iterable<I> beanIterable,
												  BeanPropertyValueChangeClosure<I>... changeClosures) {
		if (Checker.beNotEmpty(beanIterable) && Checker.beNotEmpty(changeClosures)) {
			ChainedClosure<I> chainClosure = (ChainedClosure<I>) ChainedClosure.chainedClosure(changeClosures);
			IterableUtils.forEach(beanIterable, chainClosure);
			return true;
		}
		return false;
	}

	/**
	 * 将循环体中的多个属性进行修改，按照<code>propertyValueMap<属性名,属性值></code>方式修改
	 *
	 * @param <I>              循环体中对象类型
	 * @param beanIterable     循环体
	 * @param propertyValueMap 属性，属性值映射表
	 * @return 修改成功为true,否则为false
	 */
	public static <I> boolean changePropertyValue(final Iterable<I> beanIterable,
												  Map<String, Object> propertyValueMap) {
		if (Checker.beNotEmpty(beanIterable) && Checker.beNotEmpty(propertyValueMap)) {
			MapIterator<String, Object> mapIter = MapUtils.iterableMap(propertyValueMap).mapIterator();
			List<BeanPropertyValueChangeClosure<I>> changedList = new LinkedList<>();
			while (mapIter.hasNext()) {
				changedList.add(new BeanPropertyValueChangeClosure<I>(mapIter.next(), mapIter.getValue()));
			}
			ChainedClosure<I> chainClosure = (ChainedClosure<I>) ChainedClosure.chainedClosure(changedList);
			IterableUtils.forEach(beanIterable, chainClosure);
			return true;
		}
		return false;
	}

	/**
	 * 将循环体中属性为<code>propertyName</code>对应的值以set的形式返回
	 *
	 * @param <T>          返回的set集合中的对象类型
	 * @param <I>          循环体中的对象类型
	 * @param beanIterable 循环体
	 * @param propertyName 属性名称
	 * @return 循环体中属性对应的值集合
	 */
	public static <T, I> Set<T> hashSetPropertyValue(Iterable<I> beanIterable, String propertyName) {
		return getPropertyValueCollection(beanIterable, propertyName, new LinkedHashSet<T>());
	}

	/**
	 * 将循环体属性为<code>propertyName</code> 对应的值封装成<code>returnCollection</code>集合返回
	 *
	 * @param <T>              返回集合对应的对象类型
	 * @param <I>              循环体中的对象类型
	 * @param <K>              传入的集合中对象类型
	 * @param beanIterable     循环体
	 * @param propertyName     属性名称
	 * @param returnCollection 要封装的集合
	 * @return 循环体中属性对应的值的集合
	 */
	private static <T, I, K extends Collection<T>> K getPropertyValueCollection(Iterable<I> beanIterable,
																				String propertyName, K returnCollection) {
		Validate.notNull(returnCollection, "returnCollection can't be null!");
		if (!Checker.beNotEmpty(beanIterable)) {// 避免null point
			return returnCollection;
		}

		for (I bean : beanIterable) {
			returnCollection.add(BeanUtil.<T>getProperty(bean, propertyName));
		}
		return returnCollection;
	}

	/**
	 * 将循环体中属性为<code>propertyName</code> 的值以列表的方式返回
	 *
	 * @param <T>          返回列表中对象类型
	 * @param <I>          循环体对象类型
	 * @param beanIterable 循环体
	 * @param propertyName 属性名称
	 * @return 循环体中属性对应值列表
	 */
	public static <T, I> List<T> listPropertyValue(Iterable<I> beanIterable, String propertyName) {
		if (beanIterable == null) {
			return new ArrayList<T>();
		}
		List<T> list = new ArrayList<>();
		for (I bean : beanIterable) {
			list.add(BeanUtil.<T>getProperty(bean, propertyName));
		}
		return list;
	}

	/**
	 * 将循环体中属性为<code>propertyName</code> 的值以列表的方式返回(值不重复)
	 *
	 * @param <T>          返回列表中对象类型
	 * @param <I>          循环体对象类型
	 * @param beanIterable 循环体
	 * @param propertyName 属性名称
	 * @return 循环体中属性对应值列表
	 */
	public static <T, I> List<T> listNoRepeatPropertyValue(Iterable<I> beanIterable, String propertyName) {
		if (beanIterable == null) {
			return new ArrayList<T>();
		}
		List<T> list = new ArrayList<>();
		for (I bean : beanIterable) {
			list.add(BeanUtil.<T>getProperty(bean, propertyName));
		}
		return new ArrayList<T>(new HashSet<>(list));
	}

	/**
	 * 将循环体中以属性<code>keyPropertyName</code>对应的值为key，以属性<code>valuePropertyName</code>对应的值为value
	 * 返回map
	 *
	 * @param <K>               map对应的key类型
	 * @param <V>               map对应的value类型
	 * @param <I>               循环体对象类型
	 * @param beanIterable      循环体
	 * @param keyPropertyName   key属性名称
	 * @param valuePropertyName value属性名称
	 * @return 操作后的Map
	 */
	public static <K, V, I> Map<K, V> mapPropertyValue(Iterable<I> beanIterable, String keyPropertyName,
													   String valuePropertyName) {
		if (beanIterable == null) {
			return new LinkedHashMap<K, V>();
		}
		Map<K, V> map = new LinkedHashMap<>(IterableUtils.size(beanIterable));
		for (I bean : beanIterable) {
			map.put(BeanUtil.<K>getProperty(bean, keyPropertyName), BeanUtil.<V>getProperty(bean, valuePropertyName));
		}
		return map;
	}

	/**
	 * 查找循环体中查找循环体中属性名为<code>propertyName</code>，值为<code>propertyValue</code>的对象对应的索引，并返回（第一个匹配）
	 *
	 * @param <I>           循环体中对象的类型
	 * @param <V>           属性值类型
	 * @param beanIterable  循环体
	 * @param propertyName  属性名称
	 * @param propertyValue 属性值
	 * @return 循环体中第一个匹配的对象索引
	 */
	public static <I, V> int indexOf(Iterable<I> beanIterable, String propertyName, V propertyValue) {
		return IterableUtils.indexOf(beanIterable, BeanPredicateUtil.<I, V>equalPredicate(propertyName, propertyValue));
	}

	/**
	 * 查找循环体中属性名为<code>propertyName</code>，值为<code>propertyValue</code>的对象，并返回（第一个匹配）
	 *
	 * @param <I>           循环体中对象的类型
	 * @param <V>           属性值类型
	 * @param beanIterable  循环体
	 * @param propertyName  属性名称
	 * @param propertyValue 属性值
	 * @return 第一个匹配的对象
	 */
	public static <I, V> I find(Iterable<I> beanIterable, String propertyName, V propertyValue) {
		return find(beanIterable, new BeanPredicate<>(propertyName, PredicateUtils.equalPredicate(propertyValue)));
	}

	/**
	 * 根据预言查找第一个匹配的对象
	 * <p>
	 * <b>场景:</b> 从list中查找name是 关羽,并且 age等于30的User对象
	 * </p>
	 *
	 * <pre class="code">
	 * List{@code <User>} list = toList(
	 *   new User("张飞", 23),
	 *   new User("关羽", 24),
	 *   new User("刘备", 25),
	 *   new User("关羽", 30));
	 *
	 * Map{@code <String, Object>} map = new HashMap{@code <>}();
	 * map.put("name", "关羽");
	 * map.put("age", 30);
	 *
	 * Predicate{@code <User>} predicate = BeanPredicateUtil.equalPredicate(map);
	 *
	 * User user = LoopUtil.find(list, predicate);
	 * LOGGER.debug(JsonUtil.format(user));
	 * </pre>
	 *
	 * <b>返回:</b>
	 *
	 * <pre class="code">
	 * {
	 * "age": 30,
	 * "name": "关羽"
	 * }
	 * </pre>
	 *
	 * @param <I>       循环体中对象的类型
	 * @param iterable  循环体
	 * @param predicate 预言
	 * @return 第一个匹配的对象
	 */
	public static <I> I find(Iterable<I> iterable, Predicate<I> predicate) {
		return IterableUtils.find(iterable, predicate);
	}

	/**
	 * 将循环体中 属性名称为<code>propertyName</code> 值包含在<code>propertyValues</code>的对象筛选出来
	 *
	 * @param <I>            循环体中对象的类型
	 * @param <V>            属性值类型
	 * @param beanIterable   循环体
	 * @param propertyName   属性名称
	 * @param propertyValues 属性值
	 * @return 筛选的结果列表
	 */
	@SafeVarargs
	public static <I, V> List<I> select(Iterable<I> beanIterable, String propertyName, V... propertyValues) {
		return select(beanIterable, BeanPredicateUtil.<I, V>containsPredicate(propertyName, propertyValues));
	}

	/**
	 * 将循环体中 属性名称为<code>propertyName</code> 值包含在<code>propertyValues</code>的对象筛选出来
	 *
	 * @param <I>               循环体中对象的类型
	 * @param <V>               属性值类型
	 * @param beanIterable      循环体
	 * @param propertyName      属性名称
	 * @param propertyValueList 属性值
	 * @return 筛选的结果列表
	 */
	public static <I, V> List<I> select(Iterable<I> beanIterable, String propertyName,
										Collection<V> propertyValueList) {
		return select(beanIterable, BeanPredicateUtil.<I, V>containsPredicate(propertyName, propertyValueList));
	}

	/**
	 * 筛选符合预言的对象列表
	 * <p>
	 * <b>场景:</b> 查找大于 10的元素
	 * </p>
	 *
	 * <pre class="code">
	 * Comparator{@code <Integer>} comparator = ComparatorUtils.naturalComparator();
	 * Predicate{@code <Integer>} predicate = new ComparatorPredicate{@code <Integer>}(10, comparator, Criterion.LESS);
	 *
	 * List{@code <Integer>} select = LoopUtil.select(ConvertUtil.toList(1, 5, 10, 30, 55, 88, 1, 12, 3), predicate);
	 * LOGGER.debug(JsonUtil.format(select, 0, 0));
	 * </pre>
	 *
	 * <b>返回:</b>
	 *
	 * <pre class="code">
	 * [30,55,88,12]
	 * </pre>
	 *
	 * @param <I>          循环体中对象的类型
	 * @param beanIterable 循环体
	 * @param predicate    预言
	 * @return 筛选的对象列表
	 */
	public static <I> List<I> select(Iterable<I> beanIterable, Predicate<I> predicate) {
		return (List<I>) CollectionUtils.select(beanIterable, predicate);
	}

	/**
	 * 将循环体中 属性名为<code>propertyName</code> 对应的值<span style="color:red">都不在</span>
	 * <code>propertyValues</code>列表中的对象筛选出来
	 *
	 * @param <I>            循环体中对象的类型
	 * @param <V>            属性值的对象类型
	 * @param beanIterable   循环体
	 * @param propertyName   属性名称
	 * @param propertyValues 属性值列表
	 * @return 筛选的对象列表
	 */
	@SafeVarargs
	public static <I, V> List<I> selectRejected(Iterable<I> beanIterable, String propertyName, V... propertyValues) {
		if (beanIterable == null) {
			return new LinkedList<I>();
		}
		return selectRejected(beanIterable, BeanPredicateUtil.<I, V>containsPredicate(propertyName, propertyValues));
	}

	/**
	 * 将循环体中 属性名为<code>propertyName</code> 对应的值<span style="color:red">都不在</span>
	 * <code>propertyValues</code>列表中的对象筛选出来
	 *
	 * @param <I>               循环体中对象的类型
	 * @param <V>               属性值的对象类型
	 * @param beanIterable      循环体
	 * @param propertyName      属性名称
	 * @param propertyValueList 属性值列表
	 * @return 筛选的对象列表
	 */
	public static <I, V> List<I> selectRejected(Iterable<I> beanIterable, String propertyName,
												Collection<V> propertyValueList) {
		if (beanIterable == null) {
			return new LinkedList<I>();
		}
		return selectRejected(beanIterable, BeanPredicateUtil.<I, V>containsPredicate(propertyName, propertyValueList));
	}

	/**
	 * 筛选循环体中<span style="color:red">不</span>符合预言的对象列表
	 * <p>
	 * <b>场景:</b> 从list中查找不等于1的元素
	 * </p>
	 *
	 * <pre class="code">
	 * List{@code <Long>} list = ConvertUtil.toList(1L, 1L, 2L, 3L);
	 * LoopUtil.selectRejected(list, new EqualPredicate{@code <Long>}(1L))
	 * </pre>
	 *
	 * <b>返回:</b>
	 *
	 * <pre class="code">
	 * 2L, 3L
	 * </pre>
	 *
	 * @param <I>          循环体中对象的类型
	 * @param <V>          属性值的对象类型
	 * @param beanIterable 循环体
	 * @param predicate    预言
	 * @return 筛选的对象列表
	 */
	public static <I, V> List<I> selectRejected(Iterable<I> beanIterable, Predicate<I> predicate) {
		return (List<I>) CollectionUtils.selectRejected(beanIterable, predicate);
	}

	/**
	 * 将循环体中属性为<code>propertyName</code>的值作为key，相同值的对象组成list作为value，返回映射map
	 *
	 * @param <T>          map对应KEY的对象类型
	 * @param <I>          循环体中的对象类型
	 * @param beanIterable 循环体
	 * @param propertyName 属性名
	 * @return 筛选的map映射
	 */
	public static <T, I> Map<T, List<I>> group(Iterable<I> beanIterable, String propertyName) {
		return group(beanIterable, propertyName, null);
	}

	/**
	 * 将循环体中的对象属性为<code>propertyName</code>的值作为通过<code>keyTransformer</code>转换为key，相同值的对象组成list作为value，返回映射map
	 * <p>
	 * <b>场景:</b> 从user list中,提取user的姓名的姓为key,user组成list,返回map
	 * </p>
	 *
	 * <pre class="code">
	 * User mateng55 = new User("马腾", 55);
	 * User machao28 = new User("马超", 28);
	 * User madai27 = new User("马岱", 27);
	 * User maxiu25 = new User("马休", 25);
	 * User zhangfei28 = new User("张飞", 28);
	 * User liubei32 = new User("刘备", 32);
	 * User guanyu50 = new User("关羽", 50);
	 * User guanping32 = new User("关平", 32);
	 * User guansuo31 = new User("关索", 31);
	 * User guanxing20 = new User("关兴", 18);
	 *
	 * <span style=
	 "color:green">//---------------------------------------------------------------</span>
	 * List{@code <User>} list = ConvertUtil.toList(mateng55, machao28, madai27, maxiu25, zhangfei28, liubei32, guanyu50, guanping32, guansuo31, guanxing20);
	 *
	 * <span style=
	 "color:green">//---------------------------------------------------------------</span>
	 *
	 * Map{@code <String, List<User>>} map = LoopUtil.group(list,new Transformer{@code <User, String>}(){
	 *
	 *     &#64;Override
	 *     public String transform(User user){
	 *         <span style="color:green">//提取名字 的姓</span>
	 *         return user.getName().substring(0, 1);
	 *     }
	 * });
	 *
	 * LOGGER.debug(JsonUtil.format(map));
	 *
	 * </pre>
	 *
	 * <b>返回:</b>
	 *
	 * <pre class="code">
	 *   {
	 *       "马":[{
	 *               "age": 55,
	 *               "name": "马腾",
	 *           },{
	 *               "age": 28,
	 *               "name": "马超",
	 *           },{
	 *               "age": 27,
	 *              "name": "马岱",
	 *           },{
	 *               "age": 25,
	 *               "name": "马休",
	 *           }
	 *       ],
	 *       "张": [{
	 *           "age": 28,
	 *           "name": "张飞",
	 *       }],
	 *       "刘": [{
	 *           "age": 32,
	 *           "name": "刘备",
	 *       }],
	 *       "关": [{
	 *               "age": 50,
	 *               "name": "关羽",
	 *           },{
	 *               "age": 32,
	 *               "name": "关平",
	 *           },{
	 *               "age": 31,
	 *               "name": "关索",
	 *           },{
	 *               "age": 18,
	 *               "name": "关兴",
	 *           }
	 *       ]
	 *   }
	 * </pre>
	 *
	 * @param <T>            map对应KEY的对象类型
	 * @param <I>            循环体中的对象类型
	 * @param beanIterable   循环体
	 * @param keyTransformer key转换器
	 * @return 筛选的map映射
	 */
	public static <T, I> Map<T, List<I>> group(Iterable<I> beanIterable, Transformer<I, T> keyTransformer) {
		return group(beanIterable, null, keyTransformer);
	}

	/**
	 * 将循环体中符合预言的对象属性为<code>propertyName</code>的值作为key，相同值的对象组成list作为value，返回映射map
	 *
	 * @param <T>          map对应KEY的对象类型
	 * @param <I>          循环体中的对象类型
	 * @param beanIterable 循环体
	 * @param propertyName 属性名
	 * @return 筛选的map映射
	 */
	public static <T, I> Map<T, List<I>> group(Iterable<I> beanIterable, final String propertyName,
											   Predicate<I> includePredicate) {
		Validate.notBlank(propertyName, "propertyName can't be null/empty!");
		return group(beanIterable, includePredicate, new Transformer<I, T>() {

			@Override
			public T transform(I input) {
				return BeanUtil.getProperty(input, propertyName);
			}
		});
	}

	/**
	 * 将循环体中符合预言的对象属性为<code>propertyName</code>的值作为通过<code>keyTransformer</code>转换为key，相同值的对象组成list作为value，返回映射map
	 *
	 * @param <T>              map对应KEY的对象类型
	 * @param <I>              循环体中的对象类型
	 * @param beanIterable
	 * @param includePredicate
	 * @param keyTransformer
	 * @return 筛选的map映射
	 */
	public static <T, I> Map<T, List<I>> group(Iterable<I> beanIterable, Predicate<I> includePredicate,
											   Transformer<I, T> keyTransformer) {
		if (!Checker.beNotEmpty(beanIterable)) {
			return emptyMap();
		}
		Validate.notNull(keyTransformer, "keyTransformer can't be null!");

		Map<T, List<I>> map = new LinkedHashMap<>(IterableUtils.size(beanIterable));
		for (I obj : beanIterable) {
			if (null != includePredicate && !includePredicate.evaluate(obj)) {
				continue;
			}
			MapUtil.putMultiValue(map, keyTransformer.transform(obj), obj);
		}
		return map;
	}

}
