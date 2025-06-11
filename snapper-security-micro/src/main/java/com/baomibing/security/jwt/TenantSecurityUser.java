package com.baomibing.security.jwt;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 自定义用户封装,封装用户token的额外信息
 * @author zening
 * @date 2019年2月28日 下午1:03:34
 * @version 1.0.0
 */
@Setter @Getter
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantSecurityUser extends SecurityUser {
	private String tenantId;
	private String rank;
	private Long score;
	private String state;
	public TenantSecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
	
	

}
