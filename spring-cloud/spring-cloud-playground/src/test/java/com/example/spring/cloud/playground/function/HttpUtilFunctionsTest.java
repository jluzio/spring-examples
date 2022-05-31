package com.example.spring.cloud.playground.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class HttpUtilFunctionsTest {

  @Autowired
  private FunctionCatalog catalog;
  @Autowired
  private TestRestTemplate rest;


  @Test
  void log() throws Exception {
    ResponseEntity<String> result = rest.exchange(
        RequestEntity.get(new URI("/users,username,log")).build(),
        String.class);
    assertThat(result.getStatusCode())
        .isEqualTo(HttpStatus.OK);
  }
}
