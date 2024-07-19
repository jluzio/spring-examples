package com.example.spring.data.redis;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;

public class TestApplication {

  @TestConfiguration(proxyBeanMethods = false)
  static class LocalDevTestcontainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope
    public RedisContainer redisContainer() {
      return new RedisContainer(DockerImages.REDIS);
    }
  }

  public static void main(String[] args) {
    SpringApplication.from(Application::main)
        .with(LocalDevTestcontainersConfig.class)
        .run(args);
  }
}