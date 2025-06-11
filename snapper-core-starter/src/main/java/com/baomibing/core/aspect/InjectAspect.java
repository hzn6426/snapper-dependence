/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.aspect;

import com.baomibing.core.context.SqlInjectContext;
import com.baomibing.tool.constant.InjectConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * InjectAspect
 *
 * @author zening 2023/3/16 11:08
 * @version 1.0.0
 **/
@Slf4j
@Aspect
//@Component
@Order(900)
public class InjectAspect {

    @Around("@annotation(com.baomibing.core.annotation.InjectSupport)")
    public Object roundBusinessAuth(ProceedingJoinPoint point) throws Throwable {
        try {
            SqlInjectContext.setIgnoreMapperPrefix(InjectConstant.TAG_FOR_SQL_INJECT_KEY, InjectConstant.TAG_FOR_SQL_INJECT_VALUE);
            Class<?> clazz = ((MethodSignature) point.getSignature()).getMethod().getDeclaringClass();
            String clazzName = getMapperClassName(clazz);
            SqlInjectContext.setIgnoreMapperPrefix(InjectConstant.MAPPER_PREFIX_FOR_SQL_INJECT, clazzName);
            return point.proceed();
        } finally {
            SqlInjectContext.clearAll();
        }

    }

    public String getMapperClassName(Class<?> methodClass) {
        String result = Strings.EMPTY;
        if (Checker.beNull(methodClass) || Checker.beNull(methodClass.getSuperclass()))
            return result;
        Class<?> superClass = methodClass.getSuperclass();
        if (!superClass.getName().contains("MBaseServiceImpl")) {
            return result;
        }
        try {
            Method method = superClass.getDeclaredMethod("reflectForMapperClass");
            method.setAccessible(true);
            Object classAInstance = methodClass.newInstance();
            result = method.invoke(classAInstance).toString();
        } catch (Exception e) {
            e.printStackTrace();
//			throw new ServerRuntimeException(ExceptionEnum.BUSINESS_CONNECT_EXECUTE_ERROR);
        }
        return result;
    }
}
