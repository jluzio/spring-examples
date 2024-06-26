package com.example.spring.data.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}