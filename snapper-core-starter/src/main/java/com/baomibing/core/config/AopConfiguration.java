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
    public BusinessTenantAuthAspect businessTenantAuthAspect() {
        return new BusinessTenantAuthAspect();
    }

    @Bean
    public BusinessTenantConnectAspect businessTenantConnectAspect() {
        return new BusinessTenantConnectAspect();
    }

    @Bean
    public InjectAspect injectAspect() {
        return new InjectAspect();
    }


}
