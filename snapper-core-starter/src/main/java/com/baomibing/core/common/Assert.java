/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.common;

import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.exception.ExceptionEnumable;
import com.baomibing.tool.util.Checker;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Date;

/**
 * 系统断言
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class Assert {
	
	private static boolean beNotNull(Object reference, boolean beStrict) {
		if (reference == null) {
			return false;
		}
		if (beStrict) {
			if (reference instanceof String) {
				return !StringUtils.isEmpty((String)reference);
			} 
			else if (reference instanceof Collection) {
				return !((Collection<?>) reference).isEmpty();
			}
		}
		return true;
	}
	
	
	public static CharSequence CheckNotEmpty(CharSequence cs, ExceptionEnumable exenum, Object...args) {
		if (!Checker.beNotEmpty(cs)) {
			throw new ServerRuntimeException(exenum, args);
		}
		return cs;
	}
	
	public static CharSequence CheckNotEmpty(CharSequence cs, Object...args) {
		if (Checker.beEmpty(cs)) {
			throw new ServerRuntimeException(ExceptionEnum.STRINGS_NOT_BE_EMPTY, args);
		}
		return cs;
	}
	
	public static <T> Iterable<T> CheckNotEmpty(Iterable<T> collection, ExceptionEnumable exenum, Object...args) {
		if (!Checker.beNotEmpty(collection)) {
			throw new ServerRuntimeException(exenum, args);
		}
		return collection;
	}
	
	public static <T> Iterable<T> CheckNotEmpty(Iterable<T> collection, Object...args) {
		if (Checker.beEmpty(collection)) {
			throw new ServerRuntimeException(ExceptionEnum.COLLECTION_NOT_BE_EMPTY, args);
		}
		return collection;
	}
	
	
	
	public static <T> T CheckNotNull(T reference, ExceptionEnumable exenum, Object...args) {
		if (!Checker.beNotNull(reference)) {
			throw new ServerRuntimeException(exenum, args);
		}
		return reference;
	}
	
	public static <T> T CheckNotNull(T reference, ExceptionEnumable exenum) {
		if (!Checker.beNotNull(reference)) {
			throw new ServerRuntimeException(exenum);
		}
		return reference;
	}
	
	public static <T> Collection<T> CheckNotNull(Collection<T> collection, ExceptionEnumable exenum, Object...args) {
		if (!Checker.beNotNull(collection)) {
			throw new ServerRuntimeException(exenum, args);
		}
		return collection;
	}
	
	public static <T> Collection<T> CheckNotNull(Collection<T> collection, ExceptionEnumable exenum) {
		if (!Checker.beNotNull(collection)) {
			throw new ServerRuntimeException(exenum);
		}
		return collection;
	}
	
	
	public static <T> T CheckNotNull(boolean beStrict, T reference) {
		if (!beNotNull(reference, beStrict)) {
			throw new NullPointerException();
		}
		return reference;
	}
	
	public static <T> T CheckNotNull(T refererce) {
		if (!Checker.beNotNull(refererce)) {
			throw new NullPointerException();
		}
		return refererce;
	}



	public static <T> T CheckNull(T reference, ExceptionEnumable exenum, Object...args) {
		if (Checker.beNotNull(reference)) {
			throw new ServerRuntimeException(exenum, args);
		}
		return reference;
	}

	public static <T> T CheckNull(T reference, ExceptionEnumable exenum) {
		if (Checker.beNotNull(reference)) {
			throw new ServerRuntimeException(exenum);
		}
		return reference;
	}

	public static <T> Collection<T> CheckNull(Collection<T> collection, ExceptionEnumable exenum, Object...args) {
		if (Checker.beNotNull(collection)) {
			throw new ServerRuntimeException(exenum, args);
		}
		return collection;
	}

	public static <T> Collection<T> CheckNull(Collection<T> collection, ExceptionEnumable exenum) {
		if (Checker.beNotNull(collection)) {
			throw new ServerRuntimeException(exenum);
		}
		return collection;
	}


	public static <T> T CheckNull(boolean beStrict, T reference) {
		if (beNotNull(reference, beStrict)) {
			throw new NullPointerException();
		}
		return reference;
	}

	public static <T> T CheckNull(T refererce) {
		if (Checker.beNotNull(refererce)) {
			throw new NullPointerException();
		}
		return refererce;
	}

	
	public static void CheckArgument(String... references) {
		Preconditions.checkArgument(references != null && references.length > 0);
	}
	
	public static void CheckArgument(Object... references) {
		for (Object object : references) {
			Preconditions.checkArgument(beNotNull(object, true));
		}
//		Arrays.asList(references).stream().forEach(reference -> Preconditions.checkArgument(beNotNull(reference, beStrict)));
	}
	
	public static void CheckArgumentStrict(Object...references) {
		for (Object object : references) {
			Preconditions.checkArgument(beNotNull(object, true));
		}
	}
	
	public static <T> T CheckArgument(T reference) {
		Preconditions.checkArgument(Checker.beNotNull(reference));
		return reference;
	}
	
	public static <T> Collection<T> CheckArgument(Collection<T> collection) {
		Preconditions.checkArgument(Checker.beNotNull(collection));
		return collection;
	}
	
	public static <T> T CheckArgument(T reference, ExceptionEnumable exenum, Object...args) {
		if (!Checker.beNotNull(reference)) {
			throw new ServerRuntimeException(exenum, args);
		}
		return reference;
	}
	
	public static <T> T CheckArgument(T reference, ExceptionEnumable exenum) {
		if (!Checker.beNotNull(reference)) {
			throw new ServerRuntimeException(exenum);
		}
		return reference;
	}
	
	public static <T> Collection<T> CheckArgument(Collection<T> collection, ExceptionEnumable exenum, Object...args) {
		if (!Checker.beNotNull(collection)) {
			throw new ServerRuntimeException(exenum, args);
		}
		return collection;
	}
	
	public static <T> Collection<T> CheckArgument(Collection<T> collection, ExceptionEnumable exenum) {
		if (!Checker.beNotNull(collection)) {
			throw new ServerRuntimeException(exenum);
		}
		return collection;
	}
	
	public static <T> void CheckNotEqual(T reference, Object target, ExceptionEnumable exceptionEnum) {
		CheckArgument(reference);
		if (!reference.equals(target)) {
			throw new ServerRuntimeException(exceptionEnum);
		}
	}
	
	public static <T> void CheckEqual(T reference, Object target, ExceptionEnumable exceptionEnum) {
		CheckArgument(reference);
		if (reference.equals(target)) {
			throw new ServerRuntimeException(exceptionEnum);
		}
	}
	
	public static <T> void CheckBeGreaterThan(Number a, Number b, ExceptionEnumable exceptionEnum, Object...args) {
		if (a == null || b == null) {
			throw new IllegalArgumentException("Invalid number parameter, must not be null.");
		}
		if (a.doubleValue() - b.doubleValue() <= 0) {
			if (args != null && args.length > 0) {
				throw new ServerRuntimeException(exceptionEnum, args);
			} else {
				throw new ServerRuntimeException(exceptionEnum);
			}
		}
	}
	
	public static <T> void CheckBeGreaterOrEqualThan(Number a, Number b, ExceptionEnumable exceptionEnum, Object...args) {
		if (a == null || b == null) {
			throw new IllegalArgumentException("Invalid number parameter, must not be null.");
		}
		if (a.doubleValue() - b.doubleValue() < 0) {
			if (args != null && args.length > 0) {
				throw new ServerRuntimeException(exceptionEnum, args);
			} else {
				throw new ServerRuntimeException(exceptionEnum);
			}
		}
	}
	
	public static <T> void CheckeBeAfterThan(Date end, Date before) {
		Assert.CheckArgument(end);
		Assert.CheckArgument(before);
		if (end.before(before)) {
			throw new ServerRuntimeException(ExceptionEnum.END_TIME_MUST_BE_GREATER_THAN_START_TIME);
		}
	}
	
	
	
}
