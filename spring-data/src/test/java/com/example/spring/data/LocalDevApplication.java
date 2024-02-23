package com.example.spring.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@SpringBootApplication
public class LocalDevApplication {

  @TestConfiguration(proxyBeanMethods = false)
  static class LocalDevTestcontainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope
    public MySQLContainer<?> mySQLContainer() {
      return new MySQLContainer<>("mysql:latest")
          .withInitScript("schemas/mysql/drop.sql")
          .withInitScript("schemas/mysql/create.sql");
    }
  }

  public static void main(String[] args) {
    SpringApplication.from(Application::main)
        .with(LocalDevApplication.class)
        .run(args);
  }
}