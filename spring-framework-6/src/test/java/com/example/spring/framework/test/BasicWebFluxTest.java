package com.example.spring.framework.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebFluxTest(controllers = BasicWebFluxTest.HelloController.class)
@Import({BasicWebFluxTest.HelloController.class})
class BasicWebFluxTest {

  @Autowired
  WebTestClient webTestClient;

  @Test
  void test() {
    webTestClient.get().uri("/hello")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello World!");
  }

  @RestController
  static class HelloController {

    @GetMapping(value = "/hello")
    public String helloBody() {
      return "Hello World!";
    }

  }
}
