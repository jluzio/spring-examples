package com.example.spring.framework.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(QueryParamsController.class)
@AutoConfigureRestTestClient
class QueryParamsControllerTest {

  @Autowired
  RestTestClient restTestClient;

  @Test
  void params_default() {
    restTestClient
        .get().uri("/query-params?paramFoo=foo&paramBar=bar")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.paramFoo").isEqualTo("foo")
        .jsonPath("$.paramBar").isEqualTo("bar");
  }

  @Test
  void params_custom_setters() {
    restTestClient
        .get().uri("/query-params?paramFoo=foo&param_bar=bar")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.paramFoo").isEqualTo("foo")
        .jsonPath("$.paramBar").isEqualTo("bar");
  }
}