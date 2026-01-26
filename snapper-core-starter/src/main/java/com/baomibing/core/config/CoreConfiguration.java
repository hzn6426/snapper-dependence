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

import com.baomibing.core.common.ApplicationContextHandler;
import com.baomibing.core.common.CollectionMapperDecorator;
import com.baomibing.core.common.Encryptor;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CoreConfiguration
 *
 * @author zening 2023/6/8 20:41
 * @version 1.0.0
 **/
@Configuration
public class CoreConfiguration {

    @Bean
    public Mapper mapper() {
        return DozerBeanMapperBuilder.create().withMappingFiles("dozer-mapper.xml").build();
    }

    @Bean
    public CollectionMapperDecorator collectionMapperDecorator(Mapper mapper) {
        return new CollectionMapperDecorator(mapper);
    }

    @Bean("customerStringEncryptor")
    public StringEncryptor stringEncryptor() {
        return Encryptor.createEncryptor();
    }

    @Bean
    public ApplicationContextHandler applicationContextHandler() {
        return new ApplicationContextHandler();
    }


}
