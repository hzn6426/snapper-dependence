/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfiguration {

	@Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		//所有的鉴权，由authorization filter来做
		final List<String> permitAllEndpointList = Collections.singletonList("/**");
		return http.authorizeExchange()
			.pathMatchers(HttpMethod.OPTIONS).permitAll()
	        .pathMatchers(permitAllEndpointList.toArray(new String[0])).permitAll()//白名单配置
	        .anyExchange().authenticated()
	        .and().csrf().disable()
            .httpBasic().disable()
            .logout().disable()
            .formLogin().disable()
	        .build();
    }

}
