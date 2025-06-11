/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
