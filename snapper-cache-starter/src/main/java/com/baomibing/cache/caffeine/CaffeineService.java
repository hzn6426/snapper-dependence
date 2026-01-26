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

package com.baomibing.cache.caffeine;

import com.alibaba.fastjson.JSONObject;
import com.baomibing.cache.CacheService;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * CaffeineService
 *
 * @author zening 2023/9/4 10:45
 * @version 1.0.0
 **/
public class CaffeineService implements CacheService {

    private final Cache<String, String> cache;

    private final Cache<String, List<String>> listCache;

    private final Cache<String, Map<String, String>> hashCache;

    public CaffeineService() {
        cache = Caffeine.newBuilder()
                .initialCapacity(500)
                .maximumSize(5000)
                .build();

        listCache = Caffeine.newBuilder()
                .initialCapacity(500)
                .maximumSize(2000)
                .build();

        hashCache = Caffeine.newBuilder()
                .initialCapacity(500)
                .maximumSize(2000)
                .build();
    }

    @Override
    public void deleteByKeyPrefix(String prex) {
        Set<String> cacheKeys = keys(prex);
        cacheKeys.forEach(cache.asMap().keySet()::remove);
    }

    @Override
    public Set<String> keys(String keyPrefix) {
        if (Checker.beEmpty(keyPrefix)) {
            return Sets.newHashSet();
        }
        final String key = keyPrefix.replace(Strings.STAR, "");
        return cache.asMap().keySet().stream().filter(k -> k.startsWith(key)).collect(Collectors.toSet());
    }

    @Override
    public void set(String key, String value, long timeInSeconds) {
        cache.put(key,value);
        cache.policy().expireVariably().ifPresent(e -> e.put(key, value, timeInSeconds, TimeUnit.SECONDS));
    }


    @Override
    public void set(String key, String value) {
        cache.put(key,value);
    }

    @Override
    public String get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public long size() {
        return cache.estimatedSize();
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (Checker.beEmpty(key) || Checker.beNull(clazz)) {
            return null;
        }
        String s = get(key);
        if (Checker.beEmpty(s)) {
            return null;
        }
        return JSONObject.parseObject(s, clazz);
    }

    @Override
    public boolean setNxPx(String key, String value, long milliseconds) {
        if (Checker.beEmpty(value) || Checker.beEmpty(key)) return false;
        if (Checker.beNotEmpty(get(key))) {
            return false;
        }
        cache.policy().expireVariably().ifPresent(e -> e.put(key, value, milliseconds, TimeUnit.MILLISECONDS));
        return true;
    }

    @Override
    public String getAndSetExpire(String key, long timeInSeconds) {
        String v = get(key);
        if (Checker.beEmpty(v)) {
            return v;
        }
        set(key, v, timeInSeconds);
        return v;
    }


    @Override
    public <T> T getAndSetExpire(String key, Class<T> clazz, long timeInSeconds) {
        if (Checker.beEmpty(key) || Checker.beNull(clazz)) return null;
        String s = getAndSetExpire(key, timeInSeconds);
        if (Checker.beEmpty(s)) return null;
        return JSONObject.parseObject(s, clazz);
    }

    @Override
    public Boolean del(String key) {
        cache.asMap().remove(key);
        return true;
    }

    @Override
    public Boolean expire(String key, long time) {
        if (Checker.beEmpty(key)) {
            return false;
        }
        getAndSetExpire(key, time);
        return true;
    }

    @Override
    public Boolean hasKey(String key) {
        String v = cache.asMap().get(key);
        Map<String, String> map = hashCache.getIfPresent(key);
        List<String> list = listCache.getIfPresent(key);
        return Checker.beNotEmpty(v) || Checker.beNotEmpty(map) || Checker.beNotEmpty(list);
    }

    @Override
    public void hSet(String key, String hashKey, String value) {
        Map<String, String> map = hashCache.getIfPresent(key);
        if (Checker.beNull(map)) {
            map = Maps.newConcurrentMap();
        }
        map.put(hashKey, value);
        hashCache.put(key, map);
    }

    @Override
    public Boolean hSetAll(String key, Map<String, String> map, long timeInSecond) {
        Map<String, String> cacheMap = hashCache.getIfPresent(key);
        if (Checker.beNull(cacheMap)) {
            cacheMap = Maps.newConcurrentMap();
        }
        cacheMap.putAll(map);
        Map<String, String> finalCacheMap = cacheMap;
        if (timeInSecond <= 0) {
            hashCache.put(key, cacheMap);
            return true;
        }
        hashCache.put(key, finalCacheMap);
        hashCache.policy().expireVariably().ifPresent(e -> e.put(key, finalCacheMap, timeInSecond, TimeUnit.SECONDS));
        return true;
    }

    public String hGet(String key, String hashKey) {
        Map<String, String> map = hashCache.getIfPresent(key);
        if (Checker.beNull(map)) {
            return Strings.EMPTY;
        }
        return map.get(hashKey);
    }

    public Long del(List<String> keys) {
       keys.forEach(cache.asMap().entrySet()::remove);
       return Long.valueOf(keys.size());
    }

    public <T> List<T> gets(List<String> keys, Class<T> clazz) {
        Map<String, String> map = cache.getAllPresent(keys);
        if (Checker.beEmpty(map)) {
            return Lists.newArrayList();
        }
        List<String> values = Lists.newArrayList(map.values());
        List<T> list = Lists.newArrayList();
        for (String v : values) {
            list.add(JSONObject.parseObject(v, clazz));
        }
        return list;

    }

    public List<String> lget(String key) {
        return listCache.getIfPresent(key);
    }

    @Override
    public Long lPush(String key, String value) {
        List<String> values = lget(key);
        if (Checker.beEmpty(values)) {
            values = Lists.newArrayList();
        }
        values.add(value);
        listCache.put(key, values);
        return (long) values.size();
    }

    @Override
    public Boolean lContains(String key, String value) {
        List<String> values = lget(key);
        return Checker.beNotEmpty(values) && values.contains(value);
    }

    @Override
    public Long lPush(String key, String value, long time) {
        List<String> values = lget(key);
        if (Checker.beEmpty(values)) {
            values = Lists.newArrayList();
        }
        values.add(value);
        final List<String> immList = values;
        listCache.put(key, values);
        listCache.policy().expireVariably().ifPresent(e -> e.put(key, immList, time, TimeUnit.SECONDS));
        return (long) values.size();
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        List<String> values = lget(key);
        if (Checker.beEmpty(values)) {
            return Lists.newArrayList();
        }
        return  values.subList((int) start, (int) end);
    }
}
