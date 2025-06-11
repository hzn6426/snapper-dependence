/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.jwt;


/**
 * 无状态令牌抽象
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class StatelessToken {


	private String host;// 客户IP
	
	public StatelessToken(String host){
		this.host = host;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}