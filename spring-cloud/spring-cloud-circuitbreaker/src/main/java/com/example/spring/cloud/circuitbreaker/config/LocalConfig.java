package com.example.spring.cloud.circuitbreaker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class LocalConfig {

  @Value("${local.server.port:8080}")
  private int localServerPort;

  @Bean
  WebClient localWebClient() {
    return WebClient.create("http://localhost:%s".formatted(localServerPort));
  }

}
