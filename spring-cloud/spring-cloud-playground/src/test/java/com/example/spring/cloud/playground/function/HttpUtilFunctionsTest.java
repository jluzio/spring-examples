package com.example.spring.cloud.playground.function;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Slf4j
class HttpUtilFunctionsTest {

  @Autowired
  private RestTestClient testClient;


  @Test
  void log() {
    testClient
        .get().uri("/users,username,log")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.OK);
  }
}
