package com.example.spring.data.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;

@Configuration
public class ConnectionFactoryConfig {

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    return new JedisConnectionFactory();
  }

  @Bean
  Jedis jedis(JedisConnectionFactory jedisConnectionFactory) {
    RedisConnection connection = jedisConnectionFactory.getConnection();
    return (Jedis) connection.getNativeConnection();
  }

}
