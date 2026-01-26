/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomibing.security.config;

//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableWebSecurity
public class SecurityConfiguration { //extends WebSecurityConfigurerAdapter {

//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.authenticationProvider(authProvider());
//	}
//
//	@Bean
//	public AuthenticationProvider authProvider() {
//		DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
//		impl.setUserDetailsService(userDetailService());
//		impl.setPasswordEncoder(passwordEncoder());
//		impl.setHideUserNotFoundExceptions(false);
//		return impl;
//	}
//
//	/**
//	 * 自定义访问控制，默认是所有访问都要经过认证。
//	 *
//	 * @param http
//	 * @throws Exception
//	 */
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		List<String> permitAllEndpointList = Arrays.asList("/**");
//		http.cors().and()
//			// 关闭CSRF
//			.csrf().disable()
//			.authorizeRequests()
//			// 放行所有OPTIONS请求
//			.antMatchers(HttpMethod.OPTIONS).permitAll()
//			.antMatchers(permitAllEndpointList.toArray(new String[permitAllEndpointList.size()])).permitAll()
//			.anyRequest().authenticated().and()
//			// 添加未登录与权限不足异常处理器
//			.exceptionHandling().accessDeniedHandler(accessDeniedHandler())
//			.authenticationEntryPoint(authenticationEntryPoint()).and()
//			// 关闭Session机制
//			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//	}
//
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//	@Override
//	@Bean
//	public AuthenticationManager authenticationManager() throws Exception {
//		return super.authenticationManager();
//	}
//
//	@Bean
//	public UserDetailsService userDetailService() {
//		return new CommonUserDetailManager();
//	}
//
//	@Bean
//	public AccessDeniedHandler accessDeniedHandler() {
//		return new CommonAccessDeniedHandler();
//	}
//
//	@Bean
//	public AuthenticationEntryPoint authenticationEntryPoint() {
//		return new CommonAuthencaitonEntryPointHandler();
//	}
//
//	@Bean
//	public CommonCrosFilter commonCrosFilter() {
//		return new CommonCrosFilter();
//	}
//
//	@Bean
//	public AuthorityWebExceptionHandler authorityWebExceptionHandler() {
//		return new AuthorityWebExceptionHandler();
//	}
//
//	@Bean
//	public SystemService systemService() {
//		return new SystemServiceImpl();
//	}
//
//	@Bean
//	public SystemTenantService systemTenantService() {
//		return new SystemTenantServiceImpl();
//	}
}
