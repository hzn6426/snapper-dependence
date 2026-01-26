
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

public class CreateRSAKeyException extends RuntimeException {

	private static final long serialVersionUID = 6870764020292291404L;

	public CreateRSAKeyException() {
        super();
    }

    public CreateRSAKeyException(String message) {
        super(message);
    }

    public CreateRSAKeyException(Throwable cause) {
        super(cause);
    }

    public CreateRSAKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
