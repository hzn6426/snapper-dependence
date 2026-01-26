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
package com.baomibing.core.wrap;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 组织区间（左右值）用于在mapper中映射group 以便在mapper中构造条件
 * 
 * @author zening
 * @since 1.0.0
 */
@Getter @AllArgsConstructor
public class GroupIntervalWrap implements Serializable {
	private static final long serialVersionUID = 5253363281273098752L;
	private String id;
	private int left;
	private int rght;
	
}
