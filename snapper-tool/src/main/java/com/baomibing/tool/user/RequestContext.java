/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
