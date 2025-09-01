/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.jwt;

import lombok.Getter;
import lombok.Setter;

public class JwtToken extends StatelessToken {
	@Getter @Setter
	private String jwt;
	
	 public JwtToken(String host, String jwt) {
		 super(host);
		 this.jwt = jwt;
	}

	public Object getPrincipal() {
		return this.jwt;
	}

	 
}
