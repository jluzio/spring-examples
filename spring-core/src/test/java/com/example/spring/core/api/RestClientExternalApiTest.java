package com.example.spring.core.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@SpringBootTest
@Slf4j
class RestClientExternalApiTest {

  public static final String ROOT_URI = "https://jsonplaceholder.typicode.com";

  @Configuration
  static class Config {

    @Bean
    public RestClient.Builder restClientBuilder() {
      return RestClient.builder();
    }

    @Bean
    RestClient restClient(RestClient.Builder builder) {
      return builder
          .baseUrl(ROOT_URI)
          .build();
    }
  }

  @Autowired
  RestClient restClient;

  @Test
  void test_ok() {
    var responseEntity = restClient
        .get().uri("/todos/1")
        .retrieve()
        .toEntity(String.class);
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.OK);

    var responseBody = restClient
        .get().uri("/todos/1")
        .retrieve()
        .body(String.class);
    assertThat(responseBody)
        .isNotEmpty();
  }

  @Test
  void test_not_found() {
    assertThatThrownBy(() ->
        restClient
            .get().uri("/todos/999999")
            .retrieve()
            .body(String.class)
    )
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class)
        .satisfies(throwable -> {
          RestClientResponseException restClientResponseException = (RestClientResponseException) throwable;
          log.debug("responseBody: {}", restClientResponseException.getResponseBodyAsString());
        });
  }

}
