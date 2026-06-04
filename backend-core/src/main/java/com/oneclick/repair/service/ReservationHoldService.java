package com.oneclick.repair.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationHoldService {

    private final StringRedisTemplate redisTemplate;

    public boolean holdProduct(UUID productId, UUID reservationId, Duration ttl) {
        String key = key(productId);
        Boolean stored = redisTemplate.opsForValue().setIfAbsent(key, reservationId.toString(), ttl);
        return Boolean.TRUE.equals(stored);
    }

    public void releaseHold(UUID productId, UUID reservationId) {
        String key = key(productId);
        String current = redisTemplate.opsForValue().get(key);
        if (reservationId.toString().equals(current)) {
            redisTemplate.delete(key);
        }
    }

    private String key(UUID productId) {
        return "stock:hold:product:" + productId;
    }
}
