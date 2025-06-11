/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
