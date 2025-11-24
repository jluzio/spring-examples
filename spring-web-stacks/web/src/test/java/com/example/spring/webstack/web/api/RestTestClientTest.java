package com.example.spring.webstack.web.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(controllers = GreetingController.class)
@AutoConfigureRestTestClient
class RestTestClientTest {

  @Autowired
  RestTestClient restTestClient;

  @Test
  void test() throws Exception {
    restTestClient.get().uri("/hello")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello, WebMvc Spring!");
  }
}
