package com.example.spring.core.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import({BasicIntegrationTest.HelloController.class})
class BasicIntegrationTest {

  @Autowired
  WebTestClient webTestClient;
  @LocalServerPort
  int port;
  @Autowired
  TestRestTemplate testRestTemplate;

  @Test
  void test() {
    webTestClient.get().uri("/hello")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello World!");

    var uri = "http://localhost:%s/hello".formatted(port);
    assertThat(testRestTemplate.getForEntity(uri, String.class))
        .satisfies(it -> assertThat(it.getStatusCode().is2xxSuccessful()).isTrue())
        .satisfies(it -> assertThat(it.getBody()).isEqualTo("Hello World!"));
  }

  @RestController
  static class HelloController {

    @GetMapping(value = "/hello")
    public String helloBody() {
      return "Hello World!";
    }
  }
}
