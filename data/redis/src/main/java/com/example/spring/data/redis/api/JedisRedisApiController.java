package com.example.spring.data.redis.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/redis-api/jedis")
@RequiredArgsConstructor
public class JedisRedisApiController {

  private final Jedis jedis;
  private final ObjectMapper objectMapper;

  @PutMapping("hset")
  public void hset(@RequestParam String key, @RequestBody Map<String, String> value) {
    jedis.hset(key, value);
  }

  @GetMapping("hget")
  public Map<String, String> hget(@RequestParam String key) {
    return jedis.hgetAll(key);
  }

}
