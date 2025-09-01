/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.user;

import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.Strings;

import java.text.MessageFormat;

import static com.baomibing.tool.constant.RedisKeyConstant.CACHE_API_PREFIX;
import static com.baomibing.tool.constant.RedisKeyConstant.KEY_PREFIX_HMAC_USER;

/**
 * UserKey
 *
 * @author zening 2023/6/15 09:09
 * @version 1.0.0
 **/
public abstract class UserKey {

    //=======================================================================//
    //                     User   Key   List                                //
    //=======================================================================//

    //Redis中存储用户上下文的KEY(登录时存储)
    public static String userContextKey(String userName, String systemTag) {
        String cacheHashKey = userTagHashKey(userName, systemTag);
        return MessageFormat.format(RedisKeyConstant.KEY_USER_CONTEXT, cacheHashKey);
    }

    //Redis中存储用户上下文的HASH值(用户上下文以HASH形式存储)
    public static String userTagHashKey(String userName, String systemTag) {
        return (userName + Strings.HASH + systemTag).toLowerCase();
    }

    //Redis中存储用户的菜单
    public static String userMenuKey(String userName, String systemTag) {
        String cacheHashKey = userTagHashKey(userName, systemTag);
        return MessageFormat.format(RedisKeyConstant.KEY_USER_MENU, cacheHashKey);
    }

    //REDIS种存储按钮权限的KEY
    public static String buttonPermKey(String method, String url) {
        return CACHE_API_PREFIX +  method + Strings.DOUBLE_AT + url;
    }

    //REDIS中按钮权限列表存储的KEY
    public static String buttonPermContextKey() {
        return RedisKeyConstant.KEY_BUTTON_PERM_CONTEXT;
    }

    //REDIS中hmac用户列表中存储的KEY
    public static String hmacPermContextKey() {
        return RedisKeyConstant.KEY_HMAC_USER_CONTEXT;
    }

    //REDIS中用户选择的部门(用户可能有多个部门)缓存的KEY,在登录时根据选择的部门获取对应的职位权限
    public static String userSelectGroupKey(String userName, String systemTag) {
        String cacheHashKey = userTagHashKey(userName, systemTag);
        return MessageFormat.format(RedisKeyConstant.CACHE_USER_LOGIN_ORG_KEY, cacheHashKey);
    }

    //REDIS中存储用户TOKEN的KEY,在共享登录模式中存储用户的TOKEN
    public static String userTokenKey(String userName) {
        return MessageFormat.format(RedisKeyConstant.KEY_USER_TOKEN, userName);
    }

    //REDIS中存储用户生成JWT的公钥KEY
    public static String userJwtRsaKey(String tag) {
        return MessageFormat.format(RedisKeyConstant.KEY_USER_SECURITY_RSA_PK, tag);
    }

    //REDIS中HMAC列表基于APPID的缓存
    public static String userHmacAppIdKey(String appId) {
        return MessageFormat.format(KEY_PREFIX_HMAC_USER, appId);
    }
}
