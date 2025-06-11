/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.hash.*;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * MD5工具类
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class Md5Util {
	@SuppressWarnings("deprecation")
	private static HashFunction hf = Hashing.md5();
	private static Charset defaultCharset = Charset.forName("UTF-8");

	/**
	 * md5 字符串
	 * 
	 * @param data 待加密的字符串
	 * @return 操作后的字符串
	 */
	public static String md5(String data) {
		HashCode hash = hf.newHasher().putString(data, defaultCharset).hash();
		return hash.toString();
	}

	/**
	 * MD5 加密转化成对应的字符编码，转换大小写
	 * 
	 * @param data        待加密字符串
	 * @param charset     编码
	 * @param isUpperCase 是否转换大写
	 * @return 操作后的字符串
	 */
	public static String md5(String data, Charset charset, boolean isUpperCase) {
		HashCode hash = hf.newHasher().putString(data, charset == null ? defaultCharset : charset).hash();
		return isUpperCase ? hash.toString().toUpperCase() : hash.toString();
	}

	/**
	 * MD5 加密字节数组 转换大小写
	 * 
	 * @param bytes       待加密字节数组
	 * @param isUpperCase 是否转换大写
	 * @return 操作后的字符串
	 */
	public static String md5(byte[] bytes, boolean isUpperCase) {
		HashCode hash = hf.newHasher().putBytes(bytes).hash();
		return isUpperCase ? hash.toString().toUpperCase() : hash.toString();
	}

	/**
	 * MD5加密文件 转换大小写
	 * 
	 * @param sourceFile  待加密文件
	 * @param isUpperCase 是否转化大写
	 * @return 操作后的字符串
	 */
	public static String md5(File sourceFile, boolean isUpperCase) {
		HashCode hash = hf.newHasher().putObject(sourceFile, new Funnel<File>() {

			private static final long serialVersionUID = 2757585325527511209L;

			@Override
			public void funnel(File from, PrimitiveSink into) {
				try {
					into.putBytes(Files.toByteArray(from));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).hash();
		return isUpperCase ? hash.toString().toUpperCase() : hash.toString();
	}

	/**
	 * 将其转换为json后，再进行md5
	 *
	 * @param object      数据源可以是任何类型的对象
	 * @param isUpperCase 结果是否大写
	 * @param charset     涉及到字符串时的操作编码，默认是utf-8
	 * @return 操作后的字符串
	 */
	public static String md5(Object object, boolean isUpperCase, Charset charset) {
		Hasher hasher = hf.newHasher();
		String json = JSON.toJSONString(object);
		HashCode hash = hasher.putString(json, charset == null ? defaultCharset : charset).hash();
		return isUpperCase ? hash.toString().toUpperCase() : hash.toString();
	}

	/**
	 * MD5 加密对象中的属性列表
	 * 
	 * @param object      只能是封装了数据的实体类，不可以是map，List等
	 * @param fieldNames  需要参与md5计算的字段属性名，如果该属性也是一个封住数据实体类话，.后跟上具体属性名即可。如：role.level.id
	 * @param isUpperCase 结果是否大写
	 * @param charset     涉及到字符串时的操作编码，默认是utf-8
	 * @return 操作后的字符串
	 */
	public static String md5(Object object, List<String> fieldNames, boolean isUpperCase, Charset charset) {
		HashCode hash = hf.newHasher().putObject(object, new Funnel<Object>() {
			private static final long serialVersionUID = -5236251432355557848L;
			@Override
			public void funnel(Object from, PrimitiveSink into) {

				Map<String, Field> allField = getAllField(object);

				for (String fieldName : fieldNames) {
					try {
						if (fieldName.contains(".")) {
							handleDeepField(object, charset, into, allField, fieldName);
						} else {
							handleField(object, charset, into, allField, fieldName);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}

		}).hash();
		return isUpperCase ? hash.toString().toUpperCase() : hash.toString();
	}

	private static void handleDeepField(Object tempValue, Charset charset, PrimitiveSink into,
			Map<String, Field> tempAllField, String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = null;
		String[] names = fieldName.split("\\.");

		for (String name : names) {
			field = tempAllField.get(name);
			if (field == null) {
				throw new NoSuchFieldException(fieldName);
			}
			field.setAccessible(true);
			tempValue = field.get(tempValue);
			field.setAccessible(false);
			tempAllField = getAllField(tempValue);
		}

		stuffFieldValue(tempValue, charset, into);
	}

	private static void handleField(Object object, Charset charset, PrimitiveSink into, Map<String, Field> allField,
			String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = allField.get(fieldName);
		if (field == null) {
			throw new NoSuchFieldException(fieldName);
		}

		field.setAccessible(true);
		Object tempValue = field.get(object);
		stuffFieldValue(tempValue, charset, into);
		field.setAccessible(false);
	}

	private static void stuffFieldValue(Object value, Charset charset, PrimitiveSink into)
			throws IllegalAccessException {

		if (value instanceof Integer) {
			into.putInt((int) value);
		} else if (value instanceof Long) {
			into.putLong((long) value);
		} else if (value instanceof Float) {
			into.putFloat((float) value);
		} else if (value instanceof Double) {
			into.putDouble((double) value);
		} else if (value instanceof Short) {
			into.putShort((short) value);
		} else if (value instanceof Byte) {
			into.putByte((byte) value);
		} else if (value instanceof Boolean) {
			into.putBoolean((boolean) value);
		} else if (value instanceof Byte) {
			into.putByte((byte) value);
		} else if (value instanceof Character) {
			into.putChar((char) value);
		} else if (value instanceof String) {
			into.putString((String) value, charset == null ? defaultCharset : charset);
		} else {
			throw new IllegalArgumentException(value.getClass() + " is not basic data type");
		}
	}

	private static Map<String, Field> getAllField(Object object) {
		Map<String, Field> fieldMap = Maps.newHashMap();

		if (object.getClass().getName().equals(Object.class.getName())) {
			return fieldMap;
		}

		Class<?> tempClass = object.getClass();
		Field[] declaredFields = null;
		while (true) {
			declaredFields = tempClass.getDeclaredFields();
			for (Field field : declaredFields) {
				fieldMap.put(field.getName(), field);
			}

			tempClass = tempClass.getSuperclass();

			if (tempClass.getName().equals(Object.class.getName())) {
				break;
			}

		}

		return fieldMap;
	}

}
