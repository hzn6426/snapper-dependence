/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.user;

import java.util.Optional;

public class EmailContext {

	private static final ThreadLocal<EmailServer> locale = new ThreadLocal<>();

	public static void putEmailServer(EmailServer server) {
		if (!exist()) {
			locale.set(server);
		}
	}

	public static Optional<EmailServer> currentEmailServer() {
		if (locale.get() == null) {
			return Optional.empty();
		}
		return Optional.of(locale.get());
	}

	public static boolean exist() {
		return currentEmailServer().isPresent();
	}

	public static void remove() {
		locale.remove();
	}


}
