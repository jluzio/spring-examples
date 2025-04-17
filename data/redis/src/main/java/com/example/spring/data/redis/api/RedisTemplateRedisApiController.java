package com.example.spring.data.redis.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis-api/template")
@RequiredArgsConstructor
public class RedisTemplateRedisApiController {

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  @GetMapping("hget")
  public Map<Object, Object> hget(@RequestParam String key) {
    return redisTemplate.opsForHash().entries(key);
  }

  @PutMapping("hset")
  public void hset(@RequestParam String key, @RequestBody Map<?, ?> value) {
    redisTemplate.opsForHash().putAll(key, value);
  }

}
