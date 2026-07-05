package com.maksud.incident.incident_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> void save(String key, T value, Duration ttl) {

        try {
            String json = objectMapper.writeValueAsString(value);

            redisTemplate.opsForValue().set(key, json, ttl);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize object", e);
        }
    }

    public <T> Optional<T> get(String key, Class<T> clazz) {

        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(json, clazz));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to deserialize object", e);
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

}