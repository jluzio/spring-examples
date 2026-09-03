package com.example.spring.cloud.playground.feign;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@SpringBootTest
@Slf4j
class VariableBasePathFeignTest {

  @Configuration
  @Import(FeignTestConfig.class)
  @EnableFeignClients(clients = ExampleApi.class)
  static class Config {

  }

  @FeignClient(name = "var-basepath-example-api")
  public interface ExampleApi {

    @GetMapping("/users/{id}")
    User getUser(URI baseUri, @PathVariable("id") Integer id);

  }

  @Autowired
  ExampleApi api;

  @Test
  void test() {
    var baseUri = URI.create("https://jsonplaceholder.typicode.com");
    var user = api.getUser(baseUri, 1);
    log.info("output: {}", user);
    assertThat(user)
        .isNotNull();
  }

}
