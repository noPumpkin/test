package com.example.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    public <T> boolean set(KeyPrefix prefix,String key,T value,int exTime) {
        String str = beanToString(value);
        if (str == null || str.length() <= 0) {
            return false;
        }
        //生成唯一key
        String realKey = prefix.getPrefix() + key;
        System.out.println(str);
        //设置过期时间
        if(exTime <= 0) {
            redisTemplate.opsForValue().set(realKey, str);
        } else {
            redisTemplate.opsForValue().set(realKey, str, exTime, TimeUnit.SECONDS);
        }

        return true;
    }
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz) {
        //生成真正的key
        String realKey  = prefix.getPrefix() + key;
        String str = redisTemplate.opsForValue().get(realKey);
        T t = stringToBean(str,clazz);
        return t;
    }
    public <T> Long incr(KeyPrefix prefix, String key) {
        String realKey  = prefix.getPrefix() + key;
        return redisTemplate.opsForValue().increment(realKey);
    }
    public <T> Long decr(KeyPrefix prefix, String key) {
        //生成真正的key
        String realKey  = prefix.getPrefix() + key;
        return redisTemplate.opsForValue().decrement(realKey);
    }
    public <T> boolean exists(KeyPrefix prefix, String key) {
        String realKey  = prefix.getPrefix() + key;
        return redisTemplate.hasKey(realKey);
    }
    public <T> boolean delete(KeyPrefix prefix, String key) {
        String realKey  = prefix.getPrefix() + key;
        return redisTemplate.delete(realKey);
    }
    public static <T> String beanToString(T value) {
        if(value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class) {
            return ""+value;
        }else if(clazz == String.class) {
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class) {
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    public static <T > T stringToBean(String str, Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if(clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class) {
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class) {
            return  (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }
}
