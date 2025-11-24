package com.example.spring.framework.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = QueryParamsController.class)
class QueryParamsControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @Test
  void params_default() {
    webTestClient
        .get().uri("/query-params?paramFoo=foo&paramBar=bar")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.paramFoo").isEqualTo("foo")
        .jsonPath("$.paramBar").isEqualTo("bar");
  }

  @Test
  void params_custom_setters() {
    webTestClient
        .get().uri("/query-params?paramFoo=foo&param_bar=bar")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.paramFoo").isEqualTo("foo")
        .jsonPath("$.paramBar").isEqualTo("bar");
  }
}