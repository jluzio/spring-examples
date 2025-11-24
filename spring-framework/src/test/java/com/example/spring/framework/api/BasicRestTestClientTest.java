package com.example.spring.framework.api;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.client.RestTestClient;

class BasicRestTestClientTest {

  @Test
  void test() {
    var restTestClient = RestTestClient.bindToController(new HelloController()).build();
    restTestClient.get().uri("/hello")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello World!");
  }

}
