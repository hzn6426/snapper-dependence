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
package com.baomibing.web.config;

/**
 * WebMvc 通用配置类
 * 
 * @author zening
 * @since 1.0.0
 */
//@ConditionalOnProperty(prefix = "spring.mvc", name = "enabled", havingValue = "true", matchIfMissing = true)
//@Configuration
public class MvcConfigurer //implements WebMvcConfigurer
{

//	@Autowired
//	private MappingJackson2HttpMessageConverter jacksonMessageConverter;
//	@Autowired
//	private StringHttpMessageConverter stringMessageConvert;
//
//	@Bean
//	public ContextHandlerInterceptor contextInterceptor() {
//		return new ContextHandlerInterceptor();
//	}
//
////	@Bean
////	public TenantHandlerInterceptor tenantInterceptor() {
////		return new TenantHandlerInterceptor();
////	}
//
//	@Bean
//	public ReturnHandlerAdvice returnHandlerAdvice() {
//		return  new ReturnHandlerAdvice();
//	}
//
//	@Bean
//	public GlobalExceptionHandler globalExceptionHandler() {
//		return new GlobalExceptionHandler();
//	}
//
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(contextInterceptor()).excludePathPatterns(Lists.newArrayList("/activiti/**"))
//				.addPathPatterns("/**").order(-1);
//
////		registry.addInterceptor(tenantInterceptor()).excludePathPatterns(Lists.newArrayList("/activiti/**"))
////				.addPathPatterns("/**").order(1);
//	}
//
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//	}
//
//	@Override
//	public void addFormatters(FormatterRegistry registry) {
//		registry.addConverter(new Date2StringConverter());
//		registry.addConverter(new LocalDate2StringConverter());
//		registry.addConverter(new LocalDateTime2StringConverter());
//		registry.addConverter(new LocalTime2StringConverter());
//		registry.addConverter(new String2DateConverter());
//		registry.addConverter(new String2LocalDateConverter());
//		registry.addConverter(new String2LocalDateTimeConverter());
//		registry.addConverter(new String2LocalTimeConverter());
//	}
//
//	@Override
//	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//		converters.add(jacksonMessageConverter);
//		converters.add(stringMessageConvert);
//	}
//
//	@Bean
//	public LocalValidatorFactoryBean validatorFactoryBean() {
//		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
//		bean.getValidationPropertyMap().put("hibernate.validator.fail_fast", "true");
//		bean.setValidationMessageSource(messageSource());
//		return bean;
//	}
//
//	@Bean
//	public MessageSource messageSource() {
//		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//		messageSource.setBasename("classpath:Validation");
//		messageSource.setDefaultEncoding("UTF-8");
//		messageSource.setUseCodeAsDefaultMessage(false);
//		messageSource.setCacheSeconds(60);
//		return messageSource;
//	}
//
//	@Bean
//	public LocaleResolver localeResolver() {
//		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
//		// 指定默认语言为中文
//		localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
//		return localeResolver;
//	}
//
//	@Bean
//	@ConditionalOnClass(Undertow.class)
//	public UndertowServerFactoryCustomizer getUndertowServerFactoryCustomizer() {
//		return new UndertowServerFactoryCustomizer();
//	}

}
