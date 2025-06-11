/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 并发任务执行过程中的异常捕获处理
 * @author : zening
 * @date: 2020-08-14 14:42
 * @version: 1.0.0
 */
@Slf4j
public class EventHandlerException<T> implements ExceptionHandler<T> {
    @Override
    public void handleEventException(Throwable throwable, long sequence, Object event) {
    	throwable.printStackTrace();
//        throwable.fillInStackTrace();
        log.error("process data error sequence ==[{}] event==[{}] ,ex ==[{}]", sequence, event.toString(), throwable.getMessage());
        throw new RuntimeException(throwable);
    }

    @Override
    public void handleOnStartException(Throwable throwable) {
        log.error("start disruptor error ==[{}]!", throwable.getMessage());
        throw new RuntimeException(throwable);
    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {
        log.error("shutdown disruptor error ==[{}]!", throwable.getMessage());
        throw new RuntimeException(throwable);
    }

}
