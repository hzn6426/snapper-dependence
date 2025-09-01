/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.config;

import com.baomibing.gateway.common.ApplicationContextHandler;
import com.baomibing.gateway.common.Encryptor;
import com.baomibing.gateway.predicate.RequestBodyPredicate;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GateWayAutoConfiguration {

    @Bean("customerStringEncryptor")
    public StringEncryptor stringEncryptor() {
        return Encryptor.createEncryptor();
    }
	@Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
	
	@Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return new DefaultServerCodecConfigurer();
    }

	@Bean
	public RequestBodyPredicate requestBodyPredicate() {
		return new RequestBodyPredicate();
	}

    @Bean
    public ApplicationContextHandler applicationContextHandler() {
        return new ApplicationContextHandler();
    }
	
//	@Bean
//	public BlackListFilter blackListFilter() {
//		return new BlackListFilter();
//	}
//
//	@Bean
//	public JwtTokenFilter jwtTokenFilter() {
//		return new JwtTokenFilter();
//	}
	
//	@Bean
//	public RateLimitFilter rateLimiterFilter() {
//		return new RateLimitFilter();
//	}
	
//	@Bean
//	public LogTraceFilter logTraceFilter() {
//		return new LogTraceFilter();
//	}
//
//
//	@Bean
//	public AuthorizationFilter authorizationFilter() {
//		return new AuthorizationFilter();
//	}

//	@Bean
//	public HmacFilter hmacFilter() {
//		return new HmacFilter();
//	}
//
//	@Bean
//	public HmacAuthorizationFilter hmacAuthorizationFilter() {
//		return new HmacAuthorizationFilter();
//	}
//
//	@Bean
//	public ThirdPartFilter thirdPartFilter() {
//		return new ThirdPartFilter();
//	}


}
