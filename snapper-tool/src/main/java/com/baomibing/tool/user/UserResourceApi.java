/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
