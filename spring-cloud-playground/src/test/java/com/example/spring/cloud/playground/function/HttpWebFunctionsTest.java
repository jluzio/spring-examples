package com.example.spring.cloud.playground.function;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.cloud.playground.SpringCloudPlaygroundApplication;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = SpringCloudPlaygroundApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
//@org.springframework.cloud.function.context.test.FunctionalSpringBootTest.FunctionalSpringBootTest
class HttpWebFunctionsTest {

  @Autowired
  private TestRestTemplate rest;

  @Test
  void uppercase() throws Exception {
    ParameterizedTypeReference<ArrayList<String>> responseType = new ParameterizedTypeReference<>() {
    };
    ResponseEntity<ArrayList<String>> result = this.rest.exchange(
        RequestEntity.post(new URI("/uppercase")).body("hello"), responseType);
    assertThat(result.getBody())
        .hasSize(1)
        .isEqualTo(List.of("HELLO"));
  }

  @Test
  void lowercase() throws Exception {
    ResponseEntity<String> result = this.rest.exchange(
        RequestEntity.post(new URI("/lowercase")).body("Hello"), String.class);
    assertThat(result.getBody())
        .isEqualTo("hello");
  }

  @Test
  void reverse() throws Exception {
    ResponseEntity<String> result = this.rest.exchange(
        RequestEntity.post(new URI("/reverse")).body("Hello"), String.class);
    assertThat(result.getBody())
        .isEqualTo("olleH");
  }

  @Test
  void lowercase_reverse() throws Exception {
    ResponseEntity<String> result = this.rest.exchange(
        RequestEntity.post(new URI("/lowercase,reverse")).body("Hello"), String.class);
    assertThat(result.getBody())
        .isEqualTo("olleh");
  }
}
