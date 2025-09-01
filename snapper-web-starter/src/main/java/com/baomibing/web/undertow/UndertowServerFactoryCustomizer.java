/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.undertow;

import io.undertow.Undertow;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import java.io.IOException;

/**
 * undertow服务器自定义配置
 * 
 * @author zening
 * @since 1.0.0
 */
@Slf4j
public class UndertowServerFactoryCustomizer implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    /***
     * The size, in bytes, of each buffer slice. If not specified, the size is set based on the available
     * RAM of your system:
     * 512 bytes for less than 64 MB RAM
     * 1024 bytes (1 KB) for 64 MB - 128 MB RAM
     * 16384 bytes (16 KB) for more than 128 MB RAM
     */
    private final int bufferSize = 1024;

    /***
     * Boolean value that denotes if this buffer is a direct or heap pool. If not specified, the value is set based on
     * the available RAM of your system:
     * If available RAM is < 64MB, the value is set to false
     * If available RAM is >= 64MB, the value is set to true
     */
    private final boolean direct = true;

    /***
     * The maximum number of buffers to keep in the pool. Buffers will still be allocated above this limit,
     * but will not be retained if the pool is full.
     */
    private final int maxPoolSize = 1000;

    /***
     * The maximum number of buffers to cache on each thread.
     * The actual number may be lower depending on the calculated usage pattern.
     * default : 12
     */
    private final int threadLocalCacheSize = 20;

    /***
     * Init filters on the application startup.
     */
    private final boolean eagerInitFilters = true;

    private final int ioThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);
    private final int maxWorkerThreads = 5000;
    private final int coreWorkerThreads = 200;
    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        factory.setIoThreads(ioThreads);
        factory.setWorkerThreads(maxWorkerThreads);
        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(direct, bufferSize, maxPoolSize, threadLocalCacheSize));
            try {
                //An ideal size for most servers is 16KB.
                final Xnio xnio = Xnio.getInstance("nio", Undertow.class.getClassLoader());
                final XnioWorker xnioWorker = xnio.createWorker(OptionMap.builder().set(Options.WORKER_IO_THREADS, ioThreads)
                        .set(Options.WORKER_TASK_CORE_THREADS, coreWorkerThreads)
                        .set(Options.THREAD_DAEMON, true)
                        .set(Options.WORKER_TASK_MAX_THREADS, maxWorkerThreads)
                        //to bypass the Nagle-delayed ACK delay
                        .set(Options.TCP_NODELAY, true).getMap());
                webSocketDeploymentInfo.setWorker(xnioWorker);
            } catch (IOException e) {
                log.error("Error occurred while setting up undertow configurations. {}", e.getMessage());
                throw new RuntimeException(e);
            }
            deploymentInfo.setEagerFilterInit(eagerInitFilters);
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
        });
    }

}
