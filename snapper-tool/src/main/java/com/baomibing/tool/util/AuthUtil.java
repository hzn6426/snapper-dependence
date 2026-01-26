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
package com.baomibing.tool.util;

import com.baomibing.tool.constant.Strings;
import com.google.common.base.Splitter;

import java.util.List;

/**
 * AuthUtil
 *
 * @author zening 2022/4/28 16:33
 * @version 1.0.0
 */
public abstract class AuthUtil {

    public static boolean verifyTag(String userId, String tag) {
        if (Checker.beEmpty(userId) || Checker.beEmpty(tag)) {
            return false;
        }
        List<String> tagList = Splitter.on(Strings.HASH).splitToList(tag);

        if (Checker.beEmpty(tagList) && tagList.size() != 2) {
            return false;
        }

        return tag.equals(Md5Util.md5(userId + tagList.get(0)));
    }
}
