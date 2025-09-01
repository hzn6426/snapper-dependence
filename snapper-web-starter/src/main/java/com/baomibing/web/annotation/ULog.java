/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.annotation;

import java.lang.annotation.*;

/**
 * 用户日志标记
 * <p>
 * 标注在方法上，系统将访问该方法的日志记录到数据库
 * </p>
 * 
 * @author zening
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ULog {
    String value();
}
