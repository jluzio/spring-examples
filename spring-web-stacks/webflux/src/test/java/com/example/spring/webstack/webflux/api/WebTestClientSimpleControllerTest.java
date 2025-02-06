package com.example.spring.webstack.webflux.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class WebTestClientSimpleControllerTest {

  @Configuration
  static class Config {

    @Bean
    WebTestClient webTestClient() {
      return WebTestClient
          .bindToController(new GreetingController())
          .build();
    }
  }

  // Spring Boot will create a `WebTestClient` for you,
  // already configure and ready to issue requests against "localhost:RANDOM_PORT"
  @Autowired
  WebTestClient webTestClient;

  @Test
  void test_webmvc() {
    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webmvc/hello")
        .accept(MediaType.TEXT_PLAIN)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello, WebMvc Spring!");
  }

}
