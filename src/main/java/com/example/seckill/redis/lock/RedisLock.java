package com.example.seckill.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisLock {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public Boolean Lock(String key, String value) {

        boolean locked = false;
        int tryCount = 3;
        while (!locked && tryCount > 0) {
            locked = redisTemplate.opsForValue().setIfAbsent(key, value, 2, TimeUnit.MINUTES);
            tryCount--;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                log.error("线程被中断" + Thread.currentThread().getId(), e);
            }
        }
        return locked;

    }

    public boolean unlockLua(String key, String value) {
        if (key == null || value == null)
            return false;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript();
        //用于解锁的lua脚本位置
        redisScript.setLocation(new ClassPathResource("unlock.lua"));
        redisScript.setResultType(Long.class);
        //没有指定序列化方式，默认使用上面配置的
        Object result = redisTemplate.execute(redisScript, Arrays.asList(key), value);
        return result.equals(Long.valueOf(1));
    }
}
