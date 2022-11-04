package com.example.spring.core.api;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@SpringBootTest
@Slf4j
class TestRestTemplateTest {

  @Configuration
  static class Config {

  }

  TestRestTemplate testRestTemplate = new TestRestTemplate();

  @Test
  void test_ok() throws Exception {
    var responseEntity = testRestTemplate.getForEntity(
        "https://jsonplaceholder.typicode.com/todos/1",
        String.class
    );
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.OK);
  }

  @Test
  void test_not_found() throws Exception {
    var responseEntity = testRestTemplate.getForEntity(
        "https://jsonplaceholder.typicode.com/todos/999999",
        String.class
    );
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

}
