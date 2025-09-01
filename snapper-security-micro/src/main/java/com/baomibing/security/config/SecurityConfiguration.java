/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.security.config;

import com.baomibing.authority.service.SystemService;
import com.baomibing.security.exception.AuthorityWebExceptionHandler;
import com.baomibing.security.filter.CommonAccessDeniedHandler;
import com.baomibing.security.filter.CommonAuthencaitonEntryPointHandler;
import com.baomibing.security.filter.CommonCrosFilter;
import com.baomibing.security.filter.CommonUserDetailManager;
import com.baomibing.security.service.SystemServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Arrays;
import java.util.List;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Bean
	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
		impl.setUserDetailsService(userDetailService());
		impl.setPasswordEncoder(passwordEncoder());
		impl.setHideUserNotFoundExceptions(false);
		return impl;
	}

	/**
	 * 自定义访问控制，默认是所有访问都要经过认证。
	 *
	 * @param http
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		List<String> permitAllEndpointList = Arrays.asList("/**");
		http.cors().and()
			// 关闭CSRF
			.csrf().disable()
			.authorizeRequests()
			// 放行所有OPTIONS请求
			.antMatchers(HttpMethod.OPTIONS).permitAll()
			.antMatchers(permitAllEndpointList.toArray(new String[permitAllEndpointList.size()])).permitAll()
			.anyRequest().authenticated().and()
			// 添加未登录与权限不足异常处理器
			.exceptionHandling().accessDeniedHandler(accessDeniedHandler())
			.authenticationEntryPoint(authenticationEntryPoint()).and()
			// 关闭Session机制
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	public UserDetailsService userDetailService() {
		return new CommonUserDetailManager();
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new CommonAccessDeniedHandler();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new CommonAuthencaitonEntryPointHandler();
	}

	@Bean
	public CommonCrosFilter commonCrosFilter() {
		return new CommonCrosFilter();
	}

	@Bean
	public AuthorityWebExceptionHandler authorityWebExceptionHandler() {
		return new AuthorityWebExceptionHandler();
	}

	@Bean
	public SystemService systemService() {
		return new SystemServiceImpl();
	}

}
