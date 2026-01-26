/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
