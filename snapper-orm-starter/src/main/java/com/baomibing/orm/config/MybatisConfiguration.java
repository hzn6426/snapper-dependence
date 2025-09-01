/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MybatisConfiguration {

	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager manager = new DataSourceTransactionManager();
		manager.setDataSource(dataSource);
		return manager;
	}

	
	@Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //防止全表更新与删除插件
        BlockAttackInnerInterceptor baii = new BlockAttackInnerInterceptor();
        interceptor.addInnerInterceptor(baii);
        //sql性能规范插件
//		IllegalSQLInnerInterceptor isi = new IllegalSQLInnerInterceptor();
//		interceptor.addInnerInterceptor(isi);
        return interceptor;
    }
	

}
