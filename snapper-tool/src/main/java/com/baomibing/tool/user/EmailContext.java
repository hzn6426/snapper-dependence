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
