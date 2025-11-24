package com.example.spring.framework.exception;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class GlobalRestControllerExceptionHandlerWebTestClientTest {

  @TestConfiguration
  static class TestConfig {

    @RestController
    static class TestRestController {

      @GetMapping("/exception/unsupported-operation")
      public String unsupportedOperation() {
        throw new UnsupportedOperationException("Unsupported");
      }
    }
  }

  @Autowired
  WebTestClient webTestClient;

  @Test
  void handleUnsupportedOperation() {
    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/exception/unsupported-operation")
        .accept(MediaType.TEXT_PLAIN)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isEqualTo(HttpStatus.NOT_IMPLEMENTED);
  }
}