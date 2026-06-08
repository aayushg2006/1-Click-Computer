package com.oneclick.repair.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    @Value("${security.rate-limit.public.limit:30}")
    private long limit;

    @Value("${security.rate-limit.public.window-seconds:60}")
    private long windowSeconds;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void check(String bucketKey) {
        String key = "rl:" + bucketKey;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }
        if (count != null && count > limit) {
            throw new RuntimeException("Too many requests. Please try again later.");
        }
    }
}
