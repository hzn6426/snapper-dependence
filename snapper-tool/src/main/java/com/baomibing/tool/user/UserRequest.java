/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
/**
 * 用户请求
 * @author zening 
 * @version v1.0
 */
@Data
@Accessors(chain = true)
public class UserRequest implements Serializable {


	private String ip;

	private String url;

	private String fullUrl;

	private String method;

	private String params;

	private String browser;

	private String os;

	private String language;

}
