/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

/**
 * 属性工具
 * 
 * @author zening
 * @since 1.0.0
 */
@Slf4j
public abstract class PropertiesUtil {

	/**
	 * 从系统属性文件中获取相应的值
	 *
	 * @param key key
	 * @return 操作后的字符串
	 */
	public final static String key(String key) {
		return System.getProperty(key);
	}

	/**
	 * 根据Key读取Value
	 *
	 * @param filePath 属性文件
	 * @param key      需要读取的属性
	 */
	public final static String GetValueByKey(String filePath, String key) {
		Properties pps = OrderedPropertiesInstance();
		try (InputStream in = new BufferedInputStream(new FileInputStream(filePath))) {
			pps.load(in);
			return pps.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public final static Map<String, String> properties(InputStream in) {
		Map<String, String> map = new LinkedHashMap<>();
		OrderedProperties pps = OrderedPropertiesInstance();
		try {
			pps.load(in);
		} catch (IOException e) {
			log.error("load properties error:" + e.getMessage());
		}
		Set<String> set = pps.stringPropertyNames();
		for (String key : set) {
			map.put(key, pps.getProperty(key));
		}
		return map;
	}

	/**
	 * 读取Properties的全部信息
	 *
	 * @param filePath 读取的属性文件
	 * @return 操作后的map
	 */
	public final static Map<String, String> GetAllProperties(String filePath) {
		Map<String, String> map = new LinkedHashMap<>();
		InputStream is = PropertiesUtil.class.getResourceAsStream(filePath);
		try (InputStream in = new BufferedInputStream(is)) {
			return properties(in);
		} catch (IOException e) {
			log.error("load properties error");
		}
		return map;
	}

	/**
	 * 写入Properties信息
	 *
	 * @param filePath 写入的属性文件
	 * @param pKey     属性名称
	 * @param pValue   属性值
	 */
	public final static void WriteProperties(String filePath, String pKey, String pValue) throws IOException {
		Properties props = OrderedPropertiesInstance();

		props.load(new FileInputStream(filePath));
		// 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
		// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
		OutputStream fos = new FileOutputStream(filePath);
		props.setProperty(pKey, pValue);
		// 以适合使用 load 方法加载到 Properties 表中的格式，
		// 将此 Properties 表中的属性列表（键和元素对）写入输出流
		props.store(fos, "Update '" + pKey + "' value");

	}

	public static OrderedProperties OrderedPropertiesInstance() {
		return new OrderedProperties();
	}

	private static class OrderedProperties extends Properties {
		private static final long serialVersionUID = -8900444561840582679L;
		private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

		@Override
		public Enumeration<Object> keys() {
			return Collections.<Object>enumeration(keys);
		}

		@Override
		public Object put(Object key, Object value) {
			keys.add(key);
			return super.put(key, value);
		}

		@Override
		public Set<Object> keySet() {
			return keys;
		}

		@Override
		public Set<String> stringPropertyNames() {
			Set<String> set = new LinkedHashSet<String>();

			for (Object key : this.keys) {
				set.add((String) key);
			}

			return set;
		}
	}

}
