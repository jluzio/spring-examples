package com.example.spring.data.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@SuppressWarnings("java:S1488")
public class RedisTemplateConfig {

  @Bean
  StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
    var template = new StringRedisTemplate(connectionFactory);
    // additional config if necessary
    return template;
  }

}
