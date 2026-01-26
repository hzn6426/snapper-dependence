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
package com.baomibing.orm.config;

import com.baomibing.orm.runner.PermTableCacheWarmUpRunner;
import com.baomibing.orm.runner.TenantPermTableCacheWarmUpRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OrmPlusConfiguration
 *
 * @author zening 2024/3/22 15:11
 * @version 1.0.0
 **/
@Configuration
public class OrmPlusConfiguration {

    @Bean
    public PermTableCacheWarmUpRunner permTableCacheWarmUpRunner() {
        return new PermTableCacheWarmUpRunner();
    }

    @Bean
    public TenantPermTableCacheWarmUpRunner tenantPermTableCacheWarmUpRunner() {
        return new TenantPermTableCacheWarmUpRunner();
    }
}
