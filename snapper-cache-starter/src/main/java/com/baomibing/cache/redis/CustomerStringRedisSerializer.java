package com.baomibing.cache.redis;

import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;

/**
 * CustomerStringRedisSerializer
 *
 * @author zening 2024/1/8 10:28
 * @version 1.0.0
 **/
public class CustomerStringRedisSerializer extends StringRedisSerializer {

    @Value("snapper.cache.prefix:")
    private String cachePrefix;
    public CustomerStringRedisSerializer() {
        super();
    }

    public CustomerStringRedisSerializer(Charset charset) {
        super(charset);
    }

    @Override
    public String deserialize(byte[] bytes) {

        String v =  super.deserialize(bytes);
        return Checker.beEmpty(v) ? Strings.EMPTY : v.replaceFirst(cachePrefix, Strings.EMPTY);
    }

    @Override
    public byte[] serialize(String string) {
        return Checker.beEmpty(string) ? null : super.serialize(cachePrefix + string);
    }
}
