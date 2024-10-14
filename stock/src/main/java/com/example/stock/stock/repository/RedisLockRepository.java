package com.example.stock.stock.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean lock( Long key ){
        return redisTemplate
                .opsForValue()
                .setIfAbsent( generateKey(key), "lock", Duration.ofMillis(3_000));
    }

    public boolean unLock( Long key ){
        return redisTemplate.delete(generateKey(key));
    }

    private String generateKey( Long id ){
        return id.toString();
    }
}
