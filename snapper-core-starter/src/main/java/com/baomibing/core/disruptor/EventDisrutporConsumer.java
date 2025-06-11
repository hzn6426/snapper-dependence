/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.disruptor;

import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import lombok.Getter;

import java.util.List;

/**
 * 事件消费者抽象类
 * @author : zening
 * @date: 2020-08-14 13:48
 * @version: 1.0.0
 */
public abstract class EventDisrutporConsumer<T> implements EventHandler<ObjectEvent<T>>, WorkHandler<ObjectEvent<T>> {

    private boolean beDone = false;//消费者方法是否执行完成
    
    private boolean beIgnoreException = false;
//    @Getter
//    private CompleteListener oneCompleteListener;
    @Getter
    private List<CompleteListener> completeListeners = Lists.newArrayList();

    public void withCompleteListener(CompleteListener listener) {
    	this.completeListeners.add(listener);
    }
    
    public EventDisrutporConsumer<T> withIgnoreException() {
    	this.beIgnoreException = true;
    	return this;
    }

//    public void withAllCompleteListener(CompleteListener listener) {
//        this.allCompleteListeners.add(listener);
//    }

    @Override
    public void onEvent(ObjectEvent<T> event, long sequence, boolean endOfBatch) throws Exception {
    	try {
            this.onEvent(event);
    	}catch (Exception e) {
			if (!beIgnoreException) {
				throw e;
			}
		}
//        } finally {
//            if (Checker.BeNotEmpty(map)(oneCompleteListener)) {
//                oneCompleteListener.afterComplete();
//            }
////            if (endOfBatch) {
////              if (Checker.BeNotEmpty(allCompleteListeners)) {
////                  for (CompleteListener listener : allCompleteListeners) {
////                      listener.afterComplete();
////                  }
////              }
////            }
//        }
    }

    @Override
    public void onEvent(ObjectEvent<T> event) throws Exception {
        try {
            this.consume(event.getData());
        } catch (Exception e) {
        	if (!beIgnoreException) {
				throw e;
			}
		} finally {
            this.beDone = true;
            if (Checker.beNotEmpty(completeListeners)) {
	            for (CompleteListener listener : completeListeners) {
	            	if (Checker.beNotNull(listener)) {
	            		listener.afterComplete();
	            	}
	            }
            }
        }

    }

    public boolean beCompletion() {
        return beDone;
    }

    public abstract void consume(T t);
}
