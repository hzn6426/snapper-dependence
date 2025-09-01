/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.jwt;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
@Data @Accessors(chain = true)
public class JwtWrapper implements Serializable {
	private static final long serialVersionUID = 363774742132723114L;
	private Map<String, Object> claimMap;
	private String id;
	private String token;
	
}
