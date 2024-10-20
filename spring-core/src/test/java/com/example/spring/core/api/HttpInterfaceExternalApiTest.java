package com.example.spring.core.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.core.api.model.JsonPlaceholderModels.Todo;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootTest
@Log4j2
class HttpInterfaceExternalApiTest {

  @Configuration
  static class Config {

    @Bean
    TodoService todoService() {
      RestClient restClient = RestClient.builder()
          .baseUrl("https://jsonplaceholder.typicode.com")
          .build();
      return httpService(TodoService.class, restClient);
    }

    <T> T httpService(Class<T> serviceClass, RestClient restClient) {
      RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
      HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
      return httpServiceProxyFactory.createClient(serviceClass);
    }
  }

  @Autowired
  TodoService service;

  @Test
  void test() {
    assertThat(service.getTodo("1"))
        .satisfies(log::debug)
        .isNotNull();
  }

  interface TodoService {

    @GetExchange("/todos/{id}")
    Todo getTodo(@PathVariable String id);
  }
}
