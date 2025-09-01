/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.security.filter;


import com.google.common.collect.ImmutableList;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域请求过滤
 * 
 * @author zening
 * @since 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonCrosFilter extends CorsFilter {

	public CommonCrosFilter() {
        super(configurationSource());
	}
	
	private static UrlBasedCorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
//        config.setAllowedOrigins(ImmutableList.of("*"));
        config.setAllowedHeaders(ImmutableList.of("*"));
//        config.setMaxAge(36000L);
        config.setAllowedMethods(ImmutableList.of("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
