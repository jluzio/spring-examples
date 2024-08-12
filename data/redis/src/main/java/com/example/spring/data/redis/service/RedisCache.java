package com.example.spring.data.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
public class RedisCache implements Cache {

  private final Jedis jedis;
  private final String cacheName;
  private final ObjectMapper objectMapper;

  @Override
  public void put(String key, Object value) {
    try {
      String json = objectMapper.writeValueAsString(value);
      jedis.hset(cacheName, key, json);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public <T> T get(String key, Class<T> expectedType) {
    try {
      String json = jedis.hget(cacheName, key);
      return objectMapper.readValue(json, expectedType);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
