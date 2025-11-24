package com.example.spring.webstack.webflux.api;

import com.example.spring.webstack.webflux.api.GreetingController.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = GreetingController.class)
class WebFluxTestSimpleControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @Test
  void test_hello() {
    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webmvc/hello")
        .accept(MediaType.TEXT_PLAIN)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello, WebMvc Spring!");
  }

  @Test
  void test_hello_message_json() {
    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webmvc/hello-message")
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.message").isEqualTo("Hello, WebMvc Spring!")
        .jsonPath("$.target").isEqualTo("WebMvc Spring")
    ;
  }

  @Test
  void test_hello_message_object() {
    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webmvc/hello-message")
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(Message.class)
        .isEqualTo(new Message("Hello, WebMvc Spring!", "WebMvc Spring"));
  }

}
