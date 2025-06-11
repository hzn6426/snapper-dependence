/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.disruptor;

import java.util.List;

/**
 * @author : zening
 * @date: 2020-08-14 14:04
 * @version: 1.0.0
 */
public class EventDisruptorQueueFactory {

    /**
     * 创建点对点模式消费队列，同一事件会被一组消费者其中之一消费
     * @param queueSize 队列大小，一般为2的N次方
     * @param beMultiProducer 是否多个生产者
     * @param consumers 消费者列表
     * @return 生成的事件队列
     */
    @SafeVarargs
	public static <T> EventDisruptorQueue<T> getWorkPoolQueue(int queueSize, boolean beMultiProducer, EventHandlerException<ObjectEvent<T>> handler, EventDisrutporConsumer<T>... consumers) {
        return new EventDisruptorQueue<>(queueSize, beMultiProducer, handler, true, consumers);
    }

    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishPointEvent(ObjectEvent<T> event, EventHandlerException<ObjectEvent<T>> handler, EventDisrutporConsumer<T>... consumers) {

        return new EventDisruptorQueue<T>(event, handler, true, consumers);
    }
    
    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishPointEvent(List<ObjectEvent<T>> events, EventDisrutporConsumer<T>... consumers) {

        return new EventDisruptorQueue<T>(events, null, true, consumers);
    }

    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishPointEvent(EventHandlerException<ObjectEvent<T>> handler, EventDisrutporConsumer<T>... consumers) {

        return new EventDisruptorQueue<T>(handler, true, consumers);
    }

    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishPointEvent(EventDisrutporConsumer<T>... consumers) {

        return new EventDisruptorQueue<>(true, consumers);
    }

    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishPointEvent(List<ObjectEvent<T>> events, EventHandlerException<ObjectEvent<T>> handler, EventDisrutporConsumer<T>... consumers) {
        return new EventDisruptorQueue<>(events, handler, true, consumers);
    }

    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishSbuscribeEvent(ObjectEvent<T> event, EventHandlerException<ObjectEvent<T>> handler, EventDisrutporConsumer<T>... consumers) {
        return new EventDisruptorQueue<>(event, handler, false, consumers);
    }

    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishSbuscribeEvent(List<ObjectEvent<T>> events, EventHandlerException<ObjectEvent<T>> handler, EventDisrutporConsumer<T>... consumers) {
        return new EventDisruptorQueue<>(events, handler, false, consumers);
    }

    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishSbuscribeEvent(List<ObjectEvent<T>> events, EventDisrutporConsumer<T>... consumers) {

        return new EventDisruptorQueue<T>(events, null, false, consumers);
    }
    
    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishSbuscribeEvent(EventHandlerException<ObjectEvent<T>> handler, EventDisrutporConsumer<T>... consumers) {

        return new EventDisruptorQueue<>(handler, false, consumers);
    }

    @SafeVarargs
    public static <T> EventDisruptorQueue<T> publishSbuscribeEvent(EventDisrutporConsumer<T>... consumers) {

        return new EventDisruptorQueue<>(false, consumers);
    }

    /**
     * 创建发布订阅模式的消费队列，同一事件会被多个消费者并行消费
     * @param queueSize 队列大小，一般为2的N次方
     * @param beMultiProducer 是否多个生产者
     * @param consumers 消费者列表
     * @return 生成的事件队列
     */
    @SafeVarargs
    public static <T> EventDisruptorQueue<T> getHandleEventsQueue(int queueSize, boolean beMultiProducer, EventHandlerException<ObjectEvent<T>> handler, EventDisrutporConsumer<T>... consumers) {
        return new EventDisruptorQueue<>(queueSize, beMultiProducer, handler, false, consumers);
    }

}
