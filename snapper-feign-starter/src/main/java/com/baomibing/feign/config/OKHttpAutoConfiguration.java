/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.feign.config;

import com.baomibing.feign.interceptor.OKHttpLoggerInterceptor;
import com.baomibing.feign.okhttp.OkHttpSlf4jLogger;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * OKHttp配置
 * 
 * @author zening
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(okhttp3.OkHttpClient.class)
@AllArgsConstructor
public class OKHttpAutoConfiguration {

    @Autowired private MappingJackson2HttpMessageConverter jacksonMessageConverter;
    @Autowired private StringHttpMessageConverter stringMessageConvert;
	
    @Bean
    public OKHttpLoggerInterceptor testLoggingInterceptor() {
    	OKHttpLoggerInterceptor interceptor = new OKHttpLoggerInterceptor(new OkHttpSlf4jLogger());
        interceptor.setLevel(OKHttpLoggerInterceptor.Level.BODY);
        return interceptor;
    }

    /**
     * okhttp3 链接池配置
     *
     * @param connectionPoolFactory 链接池配置
     * @param httpClientProperties  httpClient配置
     * @return okhttp3.ConnectionPool
     */
    @Bean
    @ConditionalOnMissingBean(okhttp3.ConnectionPool.class)
    public okhttp3.ConnectionPool httpClientConnectionPool(
            FeignHttpClientProperties httpClientProperties,
            OkHttpClientConnectionPoolFactory connectionPoolFactory) {
        int maxTotalConnections = httpClientProperties.getMaxConnections();
        long timeToLive = httpClientProperties.getTimeToLive();
        TimeUnit ttlUnit = httpClientProperties.getTimeToLiveUnit();
        return connectionPoolFactory.create(maxTotalConnections, timeToLive, ttlUnit);
    }


    /**
     * 配置OkHttpClient
     *
     * @param httpClientFactory    httpClient 工厂
     * @param connectionPool       链接池配置
     * @param httpClientProperties httpClient配置
     * @param interceptor          拦截器
     * @return OkHttpClient
     */
    @Bean
    @ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
    public okhttp3.OkHttpClient httpClient(
            OkHttpClientFactory httpClientFactory,
            okhttp3.ConnectionPool connectionPool,
            FeignHttpClientProperties httpClientProperties,
            OKHttpLoggerInterceptor interceptor) {
        boolean followRedirects = httpClientProperties.isFollowRedirects();
        int connectTimeout = httpClientProperties.getConnectionTimeout();
        return httpClientFactory.createBuilder(httpClientProperties.isDisableSslValidation())
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(followRedirects)
                .connectionPool(connectionPool)
                .addInterceptor(interceptor)
                .build();
    }

    @Bean
    @LoadBalanced
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate(okhttp3.OkHttpClient httpClient) {
        RestTemplate lbRestTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory(httpClient));
        configMessageConverters(lbRestTemplate.getMessageConverters());
        return lbRestTemplate;
    }

    private void configMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(c -> c instanceof StringHttpMessageConverter || c instanceof MappingJackson2HttpMessageConverter);
        converters.add(stringMessageConvert);
        converters.add(jacksonMessageConverter);
    }


}
