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

package com.baomibing.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CacheService {

    public void deleteByKeyPrefix(String prex);

    public Set<String> keys(String keyPrefix);

    public void set(String key, String value, long timeInSeconds);

    public void set(String key, String value);

    public String get(String key);

    public long size();

    public <T> T get(String key, Class<T> clazz);

    public boolean setNxPx(String key, String value, long milliseconds);

    public String getAndSetExpire(String key, long timeInSeconds);

    public <T> T getAndSetExpire(String key, Class<T> clazz, long timeInSeconds);

    public Boolean del(String key);

    public Boolean expire(String key, long time);

    public Boolean hasKey(String key);

    public void hSet(String key, String hashKey, String value);

    public Boolean hSetAll(String key, Map<String, String> map, long timeInSecond);

    public String hGet(String key, String hashKey);

    public Long del(List<String> keys);

    public <T> List<T> gets(List<String> keys, Class<T> clazz);

    public Long lPush(String key, String value);

    public Boolean lContains(String key, String value);

    public Long lPush(String key, String value, long time);

    public List<String> lRange(String key, long start, long end);
}
