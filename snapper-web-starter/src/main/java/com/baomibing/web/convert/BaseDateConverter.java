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
package com.baomibing.web.convert;

import com.baomibing.tool.util.Checker;

import java.util.Map;
public abstract class BaseDateConverter {

	protected String matchKey(String source, Map<String, String> formatMap) {
        if (Checker.beEmpty(source) || Checker.beEmpty(formatMap)) {
            return null;
        }
        source = source.trim();
        for (Map.Entry<String, String> entry : formatMap.entrySet()) {
            if (source.matches(entry.getValue())) {
               return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Invalid date format:'" + source + "'");
    }

}
