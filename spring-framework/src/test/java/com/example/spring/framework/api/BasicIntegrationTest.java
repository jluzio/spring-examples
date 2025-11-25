package com.example.spring.framework.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class BasicIntegrationTest {

  @Autowired
  RestTestClient restTestClient;
  @LocalServerPort
  int port;

  @Test
  void test() {
    restTestClient.get().uri("/tests/hello")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello World!");

    String baseUrl = "http://localhost:%s".formatted(port);
    var alternativeClient = RestTestClient.bindToServer()
        .baseUrl(baseUrl)
        .build();
    alternativeClient.get().uri("/tests/hello")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello World!");

    var restClient = RestClient.builder()
        .baseUrl(baseUrl)
        .build();
    assertThat(restClient.get().uri("/tests/hello").retrieve().toEntity(String.class))
        .satisfies(it -> assertThat(it.getStatusCode().is2xxSuccessful()).isTrue())
        .satisfies(it -> assertThat(it.getBody()).isEqualTo("Hello World!"));
  }
}
