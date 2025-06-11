/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;


/**
 * 分页辅助类
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class PageUtil {



    /**
	 * <p>
	 * 计算当前分页偏移量
	 * </p>
	 *
	 * @param current 当前页
	 * @param size    每页显示数量
	 * @return 分页偏移量
	 */
    public static int offsetCurrent(int current, int size) {
        if (current > 0) {
            return (current - 1) * size;
        }
        return 0;
    }

}
