package com.example.spring.framework.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootTest
@Slf4j
class HttpServiceTest {

  @Configuration
  @ImportHttpServices(group = "custom", types = JsonPlaceholderApi.class)
  static class Config {

    RestClientHttpServiceGroupConfigurer restClientHttpServiceGroupConfigurer() {
      return groups -> groups.filterByName("custom").forEachClient(
          (group, clientBuilder) ->
              clientBuilder.defaultHeader("foo", "bar"));
    }

  }

  @HttpExchange("https://jsonplaceholder.typicode.com/")
  interface JsonPlaceholderApi {

    record User(String id, String username) {

    }

    @GetExchange("/users")
    List<User> users();

  }

  @Autowired
  JsonPlaceholderApi api;

  @Test
  void test() {
    var users = api.users();
    log.debug("users: {}", users);
    assertThat(users).isNotEmpty();
  }

}
