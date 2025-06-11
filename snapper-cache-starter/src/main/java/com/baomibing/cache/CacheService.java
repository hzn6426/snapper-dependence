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
