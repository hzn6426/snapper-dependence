/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HMAC协议对应的用户信息
 *
 * @author :zening
 * @version : 1.0.0
 */
@Getter
@AllArgsConstructor
public class HmacUser {

	private String appId;// 客户标识
	private String timestamp;// 时间戳
	private String baseString;// 待核验字符串
	private String digest;// 消息摘要
	private String host;// 客户IP
	private String url;// 请求的URL
}
