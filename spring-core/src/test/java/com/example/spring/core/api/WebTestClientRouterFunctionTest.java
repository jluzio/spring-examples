package com.example.spring.core.api;

import com.example.spring.core.api.webflux.GreetingHandler;
import com.example.spring.core.api.webflux.GreetingRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class WebTestClientRouterFunctionTest {

  @Configuration
  @Import({GreetingRouter.class, GreetingHandler.class})
  static class Config {

    @Bean
    WebTestClient webTestClient(GreetingRouter greetingRouter, GreetingHandler greetingHandler) {
      return WebTestClient
          .bindToRouterFunction(greetingRouter.greetingRouterFunctions(greetingHandler))
          .build();
    }

  }

  // Spring Boot will create a `WebTestClient` for you,
  // already configure and ready to issue requests against "localhost:RANDOM_PORT"
  @Autowired
  WebTestClient webTestClient;

  @Test
  void test_webflux() throws Exception {
    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webflux/hello")
        .accept(MediaType.TEXT_PLAIN)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello, WebFlux Spring!");
  }

}
