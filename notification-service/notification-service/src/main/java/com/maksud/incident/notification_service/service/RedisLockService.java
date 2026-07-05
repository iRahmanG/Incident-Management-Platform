package com.maksud.incident.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final StringRedisTemplate redisTemplate;

    public String acquireLock(String lockName, Duration timeout){
        String ownerId = UUID.randomUUID().toString();
        Boolean acquired  = redisTemplate.opsForValue().setIfAbsent(
                lockName,
                ownerId,
                timeout
        );
        if(Boolean.TRUE.equals(acquired)) {
            return ownerId;
        }
        return null;
    }

    public void releaseLock(String lockName, String ownerId){
        String currentOwner = redisTemplate.opsForValue().get(lockName);
        if(ownerId.equals(currentOwner)){
            redisTemplate.delete(lockName);
        }
    }
}
