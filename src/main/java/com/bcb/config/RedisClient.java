package com.bcb.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;


/**
 * Created by kx on 2017/5/12.
 */
@Slf4j
@Component
public class RedisClient {

    @SuppressWarnings("rawtypes")
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public void resetTimeOut(String key, int expireTime) {
        try {
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        Object result = null;
        //redisTemplate.setValueSerializer(new StringRedisSerializer());
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if (result == null) {
            return null;
        }
         return result.toString();
    }


}
