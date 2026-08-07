package com.example.spring.cloud.playground.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class HttpLangFunctionsTest {

  @Autowired
  private RestTestClient testClient;

  @Test
  void uppercase() {
    var result = testClient
        .post().uri("/uppercase")
        .body("hello")
        .exchange()
        .expectBody(String.class)
        .returnResult();
    assertThat(result.getResponseBody())
        .isEqualTo("HELLO");
  }

  @Test
  void uppercaseReactive() {
    var result = testClient
        .post().uri("/uppercaseReactive")
        .body("hello")
        .exchange()
        .expectBody(String[].class)
        .returnResult();
    assertThat(result.getResponseBody())
        .hasSize(1)
        .containsExactly("HELLO");
  }

  @Test
  void lowercase() {
    testClient
        .post().uri("/lowercase")
        .body("Hello")
        .exchange()
        .expectBody(String.class)
        .isEqualTo("hello");
  }

  @Test
  void reverse() {
    testClient
        .post().uri("/reverse")
        .body("Hello")
        .exchange()
        .expectBody(String.class)
        .isEqualTo("olleH");
  }

  @Test
  void lowercase_reverse() {
    testClient.post().uri("/lowercase,reverse")
        .body("Hello")
        .exchange()
        .expectBody(String.class)
        .isEqualTo("olleh");
  }
}
