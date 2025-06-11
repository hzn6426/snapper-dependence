/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
@Getter @Setter
@Accessors(chain = true)
public class JwtTokenResponse implements Serializable {
	private static final long serialVersionUID = 5909952317030940726L;
	private String access_token;
	private String jti;
	private String refresh_token;
	private boolean be2Change = Boolean.FALSE;
	private String type;//暂时用来回传antd
}
