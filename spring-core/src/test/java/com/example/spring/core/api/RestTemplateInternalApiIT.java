package com.example.spring.core.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.spring.core.http.ClientHttpRequestDataEvent;
import com.example.types.Todo;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class RestTemplateInternalApiIT {

  @TestConfiguration
  @Import({TodoController.class})
  static class Config {

    @Bean
    RestTemplate restTemplate(Collection<ClientHttpRequestInterceptor> interceptors) {
      return baseRestTemplateBuilder()
          .additionalInterceptors(interceptors)
          .build();
    }

    @Bean
    TestRestTemplate testRestTemplate(Collection<ClientHttpRequestInterceptor> interceptors) {
      return new TestRestTemplate(
          baseRestTemplateBuilder()
              .additionalInterceptors(interceptors));
    }

    RestTemplateBuilder baseRestTemplateBuilder() {
      return new RestTemplateBuilder()
          .requestFactory(
              () -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }
  }

  @Autowired
  RestTemplate restTemplate;
  @Autowired
  TestRestTemplate testRestTemplate;
  @Captor
  ArgumentCaptor<ClientHttpRequestDataEvent> eventArgCaptor;
  @LocalServerPort
  Integer serverPort;

  @Test
  void test_ok() {
    var uri = rootUri() + "/todos/1";
    var responseEntity = testRestTemplate.getForEntity(uri, Todo.class);
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    log.debug("responseEntity.body: {}", responseEntity.getBody());
    assertThat(responseEntity.getBody())
        .isNotNull();
  }

  @Test
  void test_testRestTemplate_not_found() {
    var uri = rootUri() + "/todos/999999";
    var responseEntity = testRestTemplate.getForEntity(uri, Todo.class);
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void test_restTemplate_not_found() {
    var uri = rootUri() + "/todos/999999";
    assertThatThrownBy(() -> restTemplate.getForEntity(uri, Todo.class))
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class)
        .isInstanceOf(NotFound.class)
        .satisfies(throwable -> {
          RestClientResponseException restClientResponseException = (RestClientResponseException) throwable;
          log.debug("responseBody: {}", restClientResponseException.getResponseBodyAsString());
        });
  }

  String rootUri() {
    return "http://localhost:%d".formatted(serverPort);
  }

  @RestController
  static class TodoController {

    List<Todo> todos = IntStream.rangeClosed(1, 10)
        .mapToObj(id -> new Todo()
            .withId(id)
            .withName("Todo-%s".formatted(id)))
        .toList();

    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> todo(@PathVariable Integer id) {
      log.debug("/todos/{}", id);
      return todos.stream()
          .filter(it -> Objects.equals(it.getId(), id))
          .findFirst()
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    }
  }
}
