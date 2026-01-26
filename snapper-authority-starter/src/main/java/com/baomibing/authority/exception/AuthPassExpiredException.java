
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

package com.baomibing.authority.exception;

/**
 * 授权码已过期异常
 * @author zening
 * @date May 11, 2019 8:04:24 PM
 * @version 1.0.0
 */
public class AuthPassExpiredException extends RuntimeException {


	private static final long serialVersionUID = 1979021093622903592L;

	public AuthPassExpiredException() {
        super();
    }

    public AuthPassExpiredException(String message) {
        super(message);
    }

    public AuthPassExpiredException(Throwable cause) {
        super(cause);
    }

    public AuthPassExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
