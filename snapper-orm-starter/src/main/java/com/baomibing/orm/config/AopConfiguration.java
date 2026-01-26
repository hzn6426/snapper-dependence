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

import com.baomibing.orm.perm.DataPermWeaver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AopConfiguration
 *
 * @author zening 2023/6/8 09:00
 * @version 1.0.0
 **/
@Configuration
@EnableAspectJAutoProxy
public class AopConfiguration {

    @Bean
    public DataPermWeaver dataPermWeaver() {
        return new DataPermWeaver();
    }

//    @Bean
//    public CommandLineRunner permTableCacheWarmUpRunner() {
//        return new PermTableCacheWarmUpRunner();
//    }
}
