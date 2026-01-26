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
package com.baomibing.cache.redis;

import com.alibaba.fastjson.JSONObject;
import com.baomibing.cache.CacheService;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class RedisService implements CacheService  {
	
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void deleteByKeyPrefix(String prex) {
        Set<String> keys = redisTemplate.keys(prex + Strings.STAR);
		if (Checker.beNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public Set<String> keys(String keyPrefix) {
        return this.redisTemplate.keys(keyPrefix + "*");
    }

    @Override
    public boolean setNxPx(String key, String value, long milliseconds) {
		if (Checker.beEmpty(value) || Checker.beEmpty(key))
			return false;
//		RedisCallback<String> callback = (connection) -> {
//			Object result = connection.execute("set", key.getBytes(), value.getBytes(), "NX".getBytes(), "PX".getBytes(), String.valueOf(milliseconds).getBytes());
//			if (Checker.beNull(result)) {
//				return null;
//			}
//			return result.toString();
//       };
//       String result = redisTemplate.execute(callback);
    	return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, milliseconds, TimeUnit.MILLISECONDS));
//       return Checker.beNotEmpty(result);
    }

    @Override
    public void set(String key, String value, long timeInSeconds) {
        redisTemplate.opsForValue().set(key, value, timeInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
		if (Checker.beEmpty(key))
			return null;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public long size() {
        Long size =  redisTemplate.opsForValue().size(Strings.STAR);
        return size == null ? 0 : size;
    }

    @Override
    public String getAndSetExpire(String key, long timeInSeconds) {
    	String k = get(key);
    	redisTemplate.expire(key, timeInSeconds, TimeUnit.SECONDS);
    	return k;
    }

    @Override
    public <T> T getAndSetExpire(String key, Class<T> clazz, long timeInSeconds) {
		if (Checker.beEmpty(key) || Checker.beNull(clazz))
			return null;
    	String s = getAndSetExpire(key, timeInSeconds);
		if (Checker.beEmpty(s))
			return null;
    	return JSONObject.parseObject(s, clazz);
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
    public <T> List<T> gets(List<String> keys, Class<T> clazz) {
    	List<T> list = Lists.newArrayList();
		if (Checker.beEmpty(keys) || Checker.beNull(clazz)) {
    		return list;
    	}
    	List<String> caches = redisTemplate.opsForValue().multiGet(keys);
		if (Checker.beEmpty(caches)) {
            return list;
        }
    	for (String c : caches) {
			list.add(Checker.beEmpty(c) ? null : JSONObject.parseObject(c, clazz));
    	}
    	return list;
    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long del(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    @Override
    public Boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }


    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    @Override
    public String hGet(String key, String hashKey) {
        Object o =  redisTemplate.opsForHash().get(key, hashKey);
        return ObjectUtil.toStringIfNotNull(o);
    }

    public Boolean hSet(final String key, final String hashKey, Object value, long time) {
    	return redisTemplate.execute(new SessionCallback<Boolean>() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Boolean execute(RedisOperations operations) throws DataAccessException {
				operations.opsForHash().put(key, hashKey, value);
				operations.expire(key, time, TimeUnit.SECONDS);
				return true;
			}
			
		});
    }

    public Set<Object> hKeys(String key) {
    	return redisTemplate.opsForHash().keys(key);
    }

    @Override
    public void hSet(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Boolean hSetAll(String key, Map<String, String> map, long time) {
    	return redisTemplate.execute(new SessionCallback<Boolean>() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Boolean execute(RedisOperations operations) throws DataAccessException {
				operations.opsForHash().putAll(key, map);
				operations.expire(key, time, TimeUnit.SECONDS);
				return true;
			}
			
		});
    }

    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    
    public void hDel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    
    public Long hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    
    public Long hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    
    public Set<String> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    
    public Long sAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    
    public Long sAdd(String key, long time, String... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time);
        return count;
    }

    
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    
    public Long sSize(String key) {
        Long size =  redisTemplate.opsForSet().size(key);
		return Checker.beNull(size) ? 0L : size.longValue();
    }

    
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    
    public String lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    public Long lIndexOf(String key, String value) {
        return redisTemplate.opsForList().indexOf(key, value);
    }

    @Override
    public Boolean lContains(String key, String value) {
        List<String> keys = lRange(key, 0, -1);
        return Checker.beNotEmpty(keys) && keys.contains(value);
        // for redis 6.0.6
//        Long v = lIndexOf(key, value);
//        return v != null && v >= 0;
    }

    @Override
    public Long lPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public String lpop(String key) {
    	return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public Long lPush(String key, String value, long time) {
        Long index = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time);
        return index;
    }

    
    public Long lPushAll(String key, String... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }
    
    
    public Long lPushAllLeft(String key, String... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    
    public Long lPushAll(String key, Long time, String... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, time);
        return count;
    }
    
    public Long lPushAllLeft(String key, Long time, String... values) {
        Long count = redisTemplate.opsForList().leftPushAll(key, values);
        expire(key, time);
        return count;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> lPopAll(String key) {
    	DefaultRedisScript<List<String>> script = new DefaultRedisScript<>();
    	script.setResultType((Class) List.class);
    	script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/multi_pop_all.lua")));
    	return redisTemplate.execute(script, Lists.newArrayList(key), new Object[] {});
    }
    
    public boolean lPushIfNotExist(String key, String value) {
    	DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
    	script.setResultType(Boolean.class);
    	script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/push_if_not_exist.lua")));
    	return Boolean.TRUE.equals(redisTemplate.execute(script, Lists.newArrayList(key), new Object[]{value}));
    }
    
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }
    
    
    public void lRemove(String key, List<String> vlist) {
		if (Checker.beEmpty(vlist))
			return;
    	DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
    	script.setResultType(Boolean.class);
    	script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/push_if_not_exist.lua")));
    	redisTemplate.execute(script, Lists.newArrayList(key), vlist.toArray(new Object[0]));
    }


}
