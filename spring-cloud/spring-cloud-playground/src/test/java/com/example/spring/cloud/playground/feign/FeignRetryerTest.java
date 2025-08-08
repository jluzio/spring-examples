package com.example.spring.cloud.playground.feign;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
class FeignRetryerTest {

  @Configuration
  @Import(FeignTestConfig.class)
  @EnableFeignClients(clients = ExampleRetryerApi.class)
  static class Config {

  }

  @FeignClient(name = "data-retryer-api")
  public interface ExampleRetryerApi {

    @GetMapping("/users/{id}")
    User getUser(@PathVariable("id") Integer id);

  }

  @Autowired
  ExampleRetryerApi api;

  @Test
  void test() {
    // verify in logs that retry has occurred (default config is maxAttempts=5)
    assertThatThrownBy(() -> api.getUser(1))
        .isInstanceOf(feign.RetryableException.class);
  }

}
