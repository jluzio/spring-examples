package com.example.spring.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;

public class TestApplication {

  @TestConfiguration(proxyBeanMethods = false)
  static class LocalDevTestcontainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope
    @ConditionalOnProperty(name = "mysql.enabled", havingValue = "true", matchIfMissing = true)
    public MySQLContainer<?> mySQLContainer() {
      return new MySQLContainer<>("mysql:latest")
          .withInitScript("schemas/mysql/drop.sql")
          .withInitScript("schemas/mysql/create.sql");
    }

    @Bean
    @ServiceConnection
    @RestartScope
    @ConditionalOnProperty(name = "mongodb.enabled", havingValue = "true")
    public MongoDBContainer mongodbContainer() {
      return new MongoDBContainer("mongo:latest");
    }
  }

  public static void main(String[] args) {
    SpringApplication.from(Application::main)
        .with(LocalDevTestcontainersConfig.class)
        .run(args);
  }
}