/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.service.impl;

import com.baomibing.authority.service.CodeService;
import com.baomibing.cache.CacheService;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.CharacterUtil;
import com.baomibing.tool.util.Checker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * 编码服务实现类
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class CodeServiceImpl implements CodeService {

    @Autowired private CacheService cacheService;

    @Override
	public <T> String makeOrderedSuffix(String redisKey, int paddingDigit,
             Function<T, Integer> maxIdFunction, T functionParam){
        return makeOrderedSuffix(redisKey, paddingDigit, "0", maxIdFunction,functionParam);
    }

    @Override
	public <T> String makeOrderedSuffix(String redisKey, int paddingDigit, String paddingChar
        , Function<T, Integer> maxIdFunction, T functionParam){
       return makeCodeSuffix(redisKey, paddingDigit, paddingChar, maxIdFunction, functionParam);
    }

    private <T> String makeCodeSuffix(String redisKey, int paddingDigit, String paddingChar
            , Function<T, Integer> maxIdFunction, T functionParam) {
        final String key = "_INNER_LOCK_FOR" + redisKey;
        Integer maxId;
        while (!cacheService.setNxPx(key, "LOCK", 500)) {
        }
        String redisMaxId = cacheService.get(redisKey);
        if (Checker.beEmpty(redisMaxId)) {
            maxId = maxIdFunction.apply(functionParam);
        } else {
            maxId = Integer.parseInt(redisMaxId);
        }
        maxId += 1;
        cacheService.set(redisKey, String.valueOf(maxId), 1);

        return StringUtils.leftPad(String.valueOf(maxId), paddingDigit, paddingChar);
    }

    @Override
    public <T> String makeTenantOrderedSuffix(String redisKey, int paddingDigit, BiFunction<T, T, Integer> maxIdFunction, T functionParam, T functionParam2) {
        return makeTenantOrderedSuffix(redisKey, paddingDigit, "0", maxIdFunction,functionParam, functionParam2);
    }

    private <T> String makeTenantOrderedSuffix(String redisKey, int paddingDigit, String paddingChar, BiFunction<T,T, Integer> maxIdFunction, T functionParam, T functionParam2) {
        Integer maxId = 0;
        do {
            final String key = "_INNER_TENANT_LOCK_FOR" + redisKey;
            while (!cacheService.setNxPx(key, "LOCK", 500)) {
            }
            String redisMaxId = cacheService.get(redisKey);
            if (Checker.beEmpty(redisMaxId)){
                maxId = maxIdFunction.apply(functionParam, functionParam2);
            }else {
                maxId = Integer.parseInt(redisMaxId);
            }
            maxId += 1;
        }while (!cacheService.setNxPx(redisKey, String.valueOf(maxId), 500));
        return StringUtils.leftPad(String.valueOf(maxId), paddingDigit, paddingChar);
    }

    @Override
    public String makeTenantOrderedPrefix(String redisKey, int paddingDigit, Supplier<String> maxIdFunction) {
        String maxId = "";
        do {
            final String key = "_INNER_ROOT_TENANT_LOCK_FOR" + redisKey;
            while (!cacheService.setNxPx(key, "LOCK", 500)) {
            }
            String redisMaxId = cacheService.get(redisKey);
            if (Checker.beEmpty(redisMaxId)){
                maxId = maxIdFunction.get();
            } else {
                maxId = StringUtils.leftPad(Strings.EMPTY, 3, "0");
            }
            char c1 = maxId.charAt(0);
            char c2 = maxId.charAt(1);
            char c3 = maxId.charAt(2);
            char nc3 = CharacterUtil.nextChar(c3);
            if (nc3 == '_') {
                c3 = '0';
                char nc2 = CharacterUtil.nextChar(c2);
                if (nc2 == '_') {
                    nc2 = '0';
                    char nc1 = CharacterUtil.nextChar(c1);
                    if (nc1 == '_') {
                        throw new ServerRuntimeException(ExceptionEnum.TENANT_GROUP_CODE_EXCEED);
                    } else {
                        c1 = nc1;
                    }
                } else {
                    c2 = nc2;
                }
            } else {
                c3 = nc3;
            }
            maxId = Character.toString(c1) + Character.toString(c2) + Character.toString(c3);
        }while (!cacheService.setNxPx(redisKey, String.valueOf(maxId), 500));
        return StringUtils.leftPad(maxId, paddingDigit, "0");
    }
}
