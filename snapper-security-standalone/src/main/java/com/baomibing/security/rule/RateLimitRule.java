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

package com.baomibing.security.rule;

import com.baomibing.core.common.Assert;
import com.baomibing.tool.limit.GateLimit;
import com.baomibing.tool.util.Checker;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.Getter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * RateLimitRule
 *
 * @author zening 2024/10/9 13:52
 * @version 1.0.0
 **/
public class RateLimitRule {

    @Getter
    private String uuid;
    private int totalCapacity;
    private int expireInSecond;
    private int speedInSecond;
    private boolean beReCreateCache = false;
    private boolean beReCreateBucket = false;

    private Cache<String, Bucket> bucketCache;

    public RateLimitRule(GateLimit limit) {
        Assert.CheckArgument(limit);
        this.uuid = limit.getUuid();
        setCapacityAndExpireAndSpeed(limit.getAllowVolume(), limit.getDurationInSecond(), limit.getSpeedInSecond());
        createCache();
    }

    public Bucket getBucket(String key) {
        Bucket bucket = bucketCache.getIfPresent(key);
        if (bucket == null || beReCreateBucket) {
            bucket = createNewBucket();
            bucketCache.put(key, bucket);
            beReCreateBucket = false;
        }
        return bucket;
    }

    private void createCache() {
        if (Checker.beNull(bucketCache) || beReCreateCache) {
            //10秒内，最多允许500个不同的IP/用户方法
            bucketCache = Caffeine.newBuilder()
                    .initialCapacity(totalCapacity)//初始大小
                    .maximumSize(totalCapacity)//最大数量
                    .expireAfterWrite(expireInSecond, TimeUnit.SECONDS)//数据过期时间
                    .build();
            beReCreateCache = false;
        }

    }

    private void setCapacityAndExpireAndSpeed(int total, int expire, int speed) {
        if (total != totalCapacity || expire != expireInSecond) {
            beReCreateCache = true;
        } else {
            beReCreateCache = false;
        }
        if ( total != totalCapacity  || speed != speedInSecond) {
            beReCreateBucket = true;
        } else {
            beReCreateBucket = false;
        }
        totalCapacity = total;
        expireInSecond = expire;
        speedInSecond = speed;

    }

    private Bucket createNewBucket() {
        Refill refill = Refill.intervally(totalCapacity, Duration.ofSeconds(expireInSecond));
        Bandwidth limit = Bandwidth.classic(totalCapacity, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }


}
