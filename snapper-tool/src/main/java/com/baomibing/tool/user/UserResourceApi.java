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
package com.baomibing.tool.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
/**
 * 用户资源权限-用户鉴权后缓存供gateway反序列化使用
 * @author zening 
 * @version v1.0
 */
@Data @Accessors(chain = true) 
public class UserResourceApi implements Serializable {
	private static final long serialVersionUID = -1347790881184820736L;
	private String id;
	private String resourceId;
	private String resourceType;
	private String reqUrl;
	private String reqMethod;
	private String state;
}
