package com.example.spring.core.webtest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WebTestClientTest {

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

  @Test
  void test_webmvc() throws Exception {
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
