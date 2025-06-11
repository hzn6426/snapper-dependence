package com.baomibing.cache.config;

import com.baomibing.cache.caffeine.CaffeineService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CaffeineConfiguration
 *
 * @author frog 2025/1/6 11:03
 * @version 1.0.0
 **/
@Configuration
@EnableCaching
@ConditionalOnProperty(value = "snapper.cache.mode", havingValue = "caffeine", matchIfMissing = false)
public class CaffeineConfiguration {

    @Bean
    public CaffeineService caffeineService() {
        return new CaffeineService();
    }
}
