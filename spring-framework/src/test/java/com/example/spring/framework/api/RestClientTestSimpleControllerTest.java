package com.example.spring.framework.api;

import com.example.spring.framework.api.webmvc.GreetingController;
import com.example.spring.framework.api.webmvc.GreetingController.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(GreetingController.class)
@AutoConfigureRestTestClient
class RestClientTestSimpleControllerTest {

  @Autowired
  RestTestClient restTestClient;

  @Test
  void test_hello() {
    restTestClient
        // Create a GET request to test an endpoint
        .get().uri("/greetings/hello")
        .accept(MediaType.TEXT_PLAIN)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello, WebMvc Spring!");
  }

  @Test
  void test_hello_message_json() {
    restTestClient
        // Create a GET request to test an endpoint
        .get().uri("/greetings/hello-message")
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
    restTestClient
        // Create a GET request to test an endpoint
        .get().uri("/greetings/hello-message")
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(Message.class)
        .isEqualTo(new Message("Hello, WebMvc Spring!", "WebMvc Spring"));
  }

}
