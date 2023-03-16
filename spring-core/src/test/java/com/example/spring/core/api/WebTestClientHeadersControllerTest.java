package com.example.spring.core.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.core.api.webmvc.HeadersController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@Slf4j
class WebTestClientHeadersControllerTest {

  @Configuration
  static class Config {

    @Bean
    WebTestClient webTestClient() {
      return WebTestClient
          .bindToController(new HeadersController())
          .build();
    }

  }

  // Spring Boot will create a `WebTestClient` for you,
  // already configure and ready to issue requests against "localhost:RANDOM_PORT"
  @Autowired
  WebTestClient webTestClient;

  @Test
  void test_webmvc() throws Exception {
    FluxExchangeResult<String> result = webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webmvc/headers")
        .accept(MediaType.TEXT_PLAIN)
        .header("xpto", "xpto-value")
        .header("FOO", "FOO-value")
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .returnResult(String.class);

    var responseBody = result.getResponseBody().single().block();
    log.debug("{}", responseBody);
    assertThat(responseBody).isNotEmpty();
  }

}
