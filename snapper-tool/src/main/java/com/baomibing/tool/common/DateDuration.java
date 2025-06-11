/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 日期间隔
 * 
 * @author zening
 * @since 1.0.0
 */
@Data @Accessors(chain = true)
public class DateDuration {

	private Date start;
	
	private Date end;
}
