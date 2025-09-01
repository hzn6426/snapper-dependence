/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池
 * 
 * @author zening
 * @since 1.0.0
 */
@Configuration
@EnableAsync
public class AsyncThreadPoolConfiguration implements AsyncConfigurer {

	/** 核心线程数（默认线程数） */
    private static final int corePoolSize = 20;
    /** 最大线程数 */
    private static final int maxPoolSize = 100;
    /** 允许线程空闲时间（单位：默认为秒） */
    private static final int keepAliveTime = 60;
    /** 缓冲队列大小 */
    private static final int queueCapacity = 200;
    /** 线程池名前缀 */
    private static final String threadNamePrefix = "Async-Service-";

    @Override // bean的名称，默认为首字母小写的方法名
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix(threadNamePrefix);
        // 线程池对拒绝任务的处理策略
        // CallerRunsPolicy：由调用线程（提交任务的线程）处理该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化
        executor.initialize();
        return TtlExecutors.getTtlExecutor(executor.getThreadPoolExecutor());
    }
}
