package com.example.spring.data.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

public class TestApplication {

  @TestConfiguration(proxyBeanMethods = false)
  static class LocalDevTestcontainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope
    public MongoDBContainer mongodbContainer() {
      return new MongoDBContainer(DockerImages.MONGO_DB);
    }
  }

  public static void main(String[] args) {
    SpringApplication.from(MongoDbApplication::main)
        .with(LocalDevTestcontainersConfig.class)
        .run(args);
  }
}