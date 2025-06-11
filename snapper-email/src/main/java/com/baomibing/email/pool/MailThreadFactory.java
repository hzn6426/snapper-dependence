/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.pool;

import java.text.MessageFormat;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MailThreadFactory implements ThreadFactory {

	private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
	private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
	private final ThreadGroup group;
	private final String threadNamePrefix;
	
	public  MailThreadFactory() {
		SecurityManager sm = System.getSecurityManager();
		group = sm == null ? Thread.currentThread().getThreadGroup() : sm.getThreadGroup();
		threadNamePrefix = MessageFormat.format( "FILE-POOL-{0}-thread-", POOL_NUMBER.getAndIncrement());
	}
	
	@Override
	public Thread newThread(Runnable r) {
        return new Thread(group, r, threadNamePrefix + THREAD_NUMBER.getAndIncrement());
	}

}
