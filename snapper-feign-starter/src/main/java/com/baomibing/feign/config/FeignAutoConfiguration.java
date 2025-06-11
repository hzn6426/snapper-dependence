/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.config;

import com.baomibing.feign.coder.FeignErrorDecoder;
import com.baomibing.feign.convert.CustomerJacksonModule;
import com.baomibing.feign.convert.DateFormatterRegistrar;
import com.baomibing.feign.interceptor.FeignWrapHeaderRequestInterceptor;
import com.baomibing.tool.constant.Formats;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.Lists;
import feign.Feign;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class FeignAutoConfiguration {


	
	@Bean
	@Primary
	@ConditionalOnClass(ObjectMapper.class)
	@ConditionalOnMissingBean
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		LocalDateTimeSerializer ser = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(Formats.DEFAULT_DATE_TIME_FORMAT));
		LocalDateTimeDeserializer der = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(Formats.DEFAULT_DATE_TIME_FORMAT));
		SimpleModule localDateTimeSerialization = new SimpleModule();
		localDateTimeSerialization.addSerializer(LocalDateTime.class, ser);
		localDateTimeSerialization.addDeserializer(LocalDateTime.class, der);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule()).registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module()).registerModule(localDateTimeSerialization);
		objectMapper.setLocale(Locale.SIMPLIFIED_CHINESE);
		objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		objectMapper
				// 该特性决定parser是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）。
				// 如果该属性关闭，则如果遇到这些字符，则会抛出异常。JSON标准说明书要求所有控制符必须使用引号，因此这是一个非标准的特性
				.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
				// 忽略不能转移的字符
				.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
				// 在使用spring boot +
				// jpa/hibernate，如果实体字段上加有FetchType.LAZY，并使用jackson序列化为json串时，会遇到SerializationFeature.FAIL_ON_EMPTY_BEANS异常
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				// 忽略未知字段
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				// 单引号处理
				.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
				// 接受数组作为序列化对象
				.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
				.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
				.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
				.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, false).configure(MapperFeature.AUTO_DETECT_GETTERS, true);

		objectMapper.registerModule(new CustomerJacksonModule());
		return objectMapper;
	}

	@Bean
	public MappingJackson2HttpMessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		List<MediaType> supportedMediaTypes = Lists.newArrayList();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		messageConverter.setSupportedMediaTypes(supportedMediaTypes);
		messageConverter.setObjectMapper(objectMapper);
		messageConverter.setDefaultCharset(StandardCharsets.UTF_8);
		messageConverter.setPrettyPrint(true);
		return messageConverter;
	}

	@Bean
	public StringHttpMessageConverter stringMessageConvert() {
		StringHttpMessageConverter messageConverter = new StringHttpMessageConverter();
		List<MediaType> supportedMediaTypes = Lists.newArrayList();
		supportedMediaTypes.add(MediaType.TEXT_PLAIN);
		messageConverter.setSupportedMediaTypes(supportedMediaTypes);
		messageConverter.setDefaultCharset(StandardCharsets.UTF_8);
		return messageConverter;
	}

    @Bean
    public DateFormatterRegistrar dateFormatRegister() {
        return new DateFormatterRegistrar();
    }

	@Bean
	public FeignErrorDecoder errorDecoder() {
		return new FeignErrorDecoder();
	}

	@Bean
	@Scope("prototype")
	public Feign.Builder feignBuilder() {
		return Feign.builder().client(new OkHttpClient()).decode404().errorDecoder(new FeignErrorDecoder()).requestInterceptor(feignHeaderInterceptor());
	}

	@Bean
	public FeignWrapHeaderRequestInterceptor feignHeaderInterceptor() {
		return new FeignWrapHeaderRequestInterceptor();
	}

    @Bean
    public Encoder feignFormEncoder() {
		List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();
		ObjectFactory<HttpMessageConverters> factory = () -> new HttpMessageConverters(converters);
		return new SpringFormEncoder(new SpringEncoder(factory));
    }



}
