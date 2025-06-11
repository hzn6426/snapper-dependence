/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
