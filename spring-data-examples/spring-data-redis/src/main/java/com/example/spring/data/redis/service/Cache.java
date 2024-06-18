package com.example.spring.data.redis.service;

public interface Cache {

  void put(String key, Object value);

  <T> T get(String key, Class<T> expectedType);

}
