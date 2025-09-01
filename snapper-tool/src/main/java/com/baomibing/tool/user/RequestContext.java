/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.user;

import java.util.Optional;

public class RequestContext {
	
	private static final ThreadLocal<UserRequest> locale = new ThreadLocal<>();
	
	public static void putRequest(UserRequest req) {
		if (!exist()) {
			locale.set(req);
		}
	}
	
	public static Optional<UserRequest> currentRequest() {
		if (locale.get() == null) {
			return Optional.empty();
		}
		return Optional.of(locale.get());
	}
	
	public static boolean exist() {
		return currentRequest().isPresent();
	}
	
	public static void remove() {
		locale.remove();
	}
	
	public static String reqUrl() {
		return currentRequest().map(UserRequest::getUrl).orElse(null);
	}
	
	public static String reqMethod() {
		return currentRequest().map(UserRequest::getMethod).orElse(null);
	}
	
	public static String reqIp() {
		return currentRequest().map(UserRequest::getIp).orElse(null);
	}

	public static String reqBrowser() {
		return currentRequest().map(UserRequest::getBrowser).orElse(null);
	}

	public static String reqOs() {
		return currentRequest().map(UserRequest::getOs).orElse(null);
	}

	public static String reqLanguage() {
		return currentRequest().map(UserRequest::getLanguage).orElse(null);
	}

}
