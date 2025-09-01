/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.disruptor;

import com.baomibing.core.common.Assert;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * 队列操作工具，用来初始化Disruptor
 * @author : zening
 * @date: 2020-08-14 13:53
 * @version: 1.0.0
 */
@Slf4j
public class EventDisruptorQueue<T> {
    @Getter
    private Disruptor<ObjectEvent<T>> disruptor;
    @Getter
    private RingBuffer<ObjectEvent<T>> ringBuffer;

    private final int DEFAULT_RING_SIZE = 1024;
    
    private CountDownLatch latch;

    private List<EventDisrutporConsumer<T>> consumerList = Lists.newArrayList();

    private List<ObjectEvent<T>> eventList = Lists.newArrayList();

    @SafeVarargs
    public EventDisruptorQueue(ObjectEvent<T> event, EventHandlerException<ObjectEvent<T>> handler, boolean bePintToPoint, EventDisrutporConsumer<T>... consumers) {
        Disruptor<ObjectEvent<T>> disruptor = new Disruptor<ObjectEvent<T>>(new ObjectEventFactory<>(), DEFAULT_RING_SIZE,
                Executors.defaultThreadFactory(), ProducerType.SINGLE,
                new SleepingWaitStrategy());
        if (bePintToPoint) {
            disruptor.handleEventsWithWorkerPool(consumers);
        } else {
            disruptor.handleEventsWith(consumers);
        }
        eventList.add(event);
        consumerList = Arrays.asList(consumers);
        disruptor.setDefaultExceptionHandler(Checker.beNull(handler) ? new EventHandlerException<>() : handler);
        init(disruptor);
        publishEvent(event);
    }

    @SafeVarargs
    public EventDisruptorQueue(int queueSize, List<ObjectEvent<T>> events, EventHandlerException<ObjectEvent<T>> handler, boolean bePintToPoint, EventDisrutporConsumer<T>... consumers) {
        Disruptor<ObjectEvent<T>> disruptor = new Disruptor<ObjectEvent<T>>(new ObjectEventFactory<>(), queueSize,
                Executors.defaultThreadFactory(), events.size() > 1 ? ProducerType.MULTI : ProducerType.SINGLE,
                new SleepingWaitStrategy());
        if (bePintToPoint) {
            disruptor.handleEventsWithWorkerPool(consumers);
        } else {
            disruptor.handleEventsWith(consumers);
        }
        consumerList = Arrays.asList(consumers);
        disruptor.setDefaultExceptionHandler(Checker.beNull(handler) ? new EventHandlerException<>() : handler);
        init(disruptor);
        publishEvents(events);
    }

    @SafeVarargs
    public EventDisruptorQueue(int queueSize, boolean beMultiProducer, EventHandlerException<ObjectEvent<T>> handler, boolean bePintToPoint, EventDisrutporConsumer<T>... consumers) {
        Disruptor<ObjectEvent<T>> disruptor = new Disruptor<ObjectEvent<T>>(new ObjectEventFactory<>(), queueSize,
                Executors.defaultThreadFactory(), beMultiProducer ? ProducerType.MULTI : ProducerType.SINGLE,
                new SleepingWaitStrategy());
        if (bePintToPoint) {
            disruptor.handleEventsWithWorkerPool(consumers);
        } else {
            disruptor.handleEventsWith(consumers);
        }
        consumerList = Arrays.asList(consumers);
        disruptor.setDefaultExceptionHandler(Checker.beNull(handler) ? new EventHandlerException<>() : handler);
        init(disruptor);
    }

    @SafeVarargs
    public EventDisruptorQueue(EventHandlerException<ObjectEvent<T>> handler, boolean bePintToPoint, EventDisrutporConsumer<T>... consumers) {
        Disruptor<ObjectEvent<T>> disruptor = new Disruptor<ObjectEvent<T>>(new ObjectEventFactory<>(), DEFAULT_RING_SIZE,
                Executors.defaultThreadFactory(), ProducerType.MULTI,
                new SleepingWaitStrategy());
        if (bePintToPoint) {
            disruptor.handleEventsWithWorkerPool(consumers);
        } else {
            disruptor.handleEventsWith(consumers);
        }
        consumerList = Arrays.asList(consumers);
        disruptor.setDefaultExceptionHandler(Checker.beNull(handler) ? new EventHandlerException<>() : handler);
        init(disruptor);
    }

    @SafeVarargs
    public EventDisruptorQueue(boolean bePintToPoint, EventDisrutporConsumer<T>... consumers) {
        Disruptor<ObjectEvent<T>> disruptor = new Disruptor<ObjectEvent<T>>(new ObjectEventFactory<>(), DEFAULT_RING_SIZE,
                Executors.defaultThreadFactory(), ProducerType.MULTI,
                new SleepingWaitStrategy());
        if (bePintToPoint) {
            disruptor.handleEventsWithWorkerPool(consumers);
        } else {
            disruptor.handleEventsWith(consumers);
        }
        consumerList = Arrays.asList(consumers);
        disruptor.setDefaultExceptionHandler(new EventHandlerException<>());
        init(disruptor);
    }

    @SafeVarargs
	public EventDisruptorQueue(List<ObjectEvent<T>> events, EventHandlerException<ObjectEvent<T>> handler, boolean bePintToPoint, EventDisrutporConsumer<T>... consumers) {
        Disruptor<ObjectEvent<T>> disruptor = new Disruptor<ObjectEvent<T>>(new ObjectEventFactory<>(), DEFAULT_RING_SIZE,
                Executors.defaultThreadFactory(), events.size() > 1 ? ProducerType.MULTI : ProducerType.SINGLE,
                new SleepingWaitStrategy());
        if (bePintToPoint) {
            disruptor.handleEventsWithWorkerPool(consumers);
        } else {
            disruptor.handleEventsWith(consumers);
        }
        eventList = events;
        consumerList = Arrays.asList(consumers);
        disruptor.setDefaultExceptionHandler(Checker.beNull(handler) ? new EventHandlerException<>() : handler);
        init(disruptor);
        publishEvents(events);
        try {
			latch.await();
			log.debug("The Disruptor will be shuting down....");
			this.shutdown();
			log.debug("The Disruptor has been shut down completely!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.shutdown();
		}
		
    }
    
    
    public void waitingForShutdown() {
		try {
			while(true) {
				latch.await();
				log.debug("The Disruptor will be shuting down....");
				this.shutdown();
				log.debug("The Disruptor has been shut down completely!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.shutdown();
		}
		
    }

    //添加消费者钩子，在最后一个执行完成后关闭disruptor
    public void addConsumerHook() {
    	if (Checker.beNotEmpty(eventList)) {
    		latch = new CountDownLatch(eventList.size());
    	}
        if (Checker.beEmpty(consumerList)) return;
        for (EventDisrutporConsumer<T> consumer : consumerList) {
            consumer.withCompleteListener(() -> {
                if (Checker.beNotNull(latch)) {
                	latch.countDown();
                }
            });
        }
    }

    //初始化disruptor
    public void init(Disruptor<ObjectEvent<T>> disruptor) {
        Assert.CheckArgument(disruptor);
        this.disruptor = disruptor;
        this.ringBuffer = this.disruptor.getRingBuffer();
        addConsumerHook();
        this.disruptor.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                disruptor.shutdown();
            }
        });
    }

    //发布事件
    public void publishEvent(ObjectEvent<T> event) {
        Assert.CheckArgument(event);
        add(event.getData());
    }

    //发布多个事件
    public void publishEvents(List<ObjectEvent<T>> events) {
        Assert.CheckArgument(events);
        events.forEach(e -> add(e.getData()));
//        addAll(datas);
    }

    //发布事件
    private void add(T t) {
        if (Checker.beNull(t)) return;
        long sequence = this.ringBuffer.next();
        try {
            ObjectEvent<T> event = this.ringBuffer.get(sequence);
            event.setData(t);
        } finally {
            this.ringBuffer.publish(sequence);
        }
    }

//    private void addAll(List<T> list) {
//        if (Checker.BeEmpty(list)) return;
//        Iterator<T> it = list.iterator();
//        while (it.hasNext()) {
//            add(it.next());
//        }
//    }

    public long cursor() {
        return this.disruptor.getRingBuffer().getCursor();
    }

    public void shutdown() {
        if (Checker.beNotNull(disruptor)) {
            this.disruptor.shutdown();
        }
    }
}
