package com.ddd.chulsi.infrastructure.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    @Async
    public void set(String key, Object value, long expiresTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expiresTime, timeUnit);
    }

    @Async
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Async
    public CompletableFuture<Boolean> hasKey(String key) {
        return CompletableFuture.completedFuture(Boolean.TRUE.equals(redisTemplate.hasKey(key)));
    }

    @Async
    public CompletableFuture<Object> get(String key) {
        return CompletableFuture.completedFuture(redisTemplate.opsForValue().get(key));
    }

}
