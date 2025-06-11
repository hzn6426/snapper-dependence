/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.config;

import com.baomibing.core.aspect.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AopConfiguration
 *
 * @author zening 2023/6/8 08:53
 * @version 1.0.0
 **/
@EnableAspectJAutoProxy
@Configuration
public class AopConfiguration {

    @Bean
    public BusinessAuthAspect businessAuthAspect() {
        return new BusinessAuthAspect();
    }

    @Bean
    public BusinessConnectAspect businessConnectAspect() {
        return new BusinessConnectAspect();
    }

    @Bean
    public InjectAspect injectAspect() {
        return new InjectAspect();
    }
}
