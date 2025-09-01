/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.annotation;

import java.lang.annotation.*;

/**
 * 对spring mvc controller返回的结果不进行封装，直接以原生结果返回
 * @author zening
 * @date 2018年4月3日 上午9:10:41
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NotWrap {
    String value() default "";
}
