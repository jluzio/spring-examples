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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class RestClientInternalApiIT {

  @TestConfiguration
  @Import({TodoController.class})
  static class Config {

    @Bean
    RestClient restClient(Collection<ClientHttpRequestInterceptor> interceptors) {
      return baseRestClientBuilder()
          .requestInterceptors(requestInterceptors -> requestInterceptors.addAll(interceptors))
          .build();
    }

    RestClient.Builder baseRestClientBuilder() {
      return RestClient.builder()
          .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }
  }

  @Autowired
  RestClient restClient;
  @Captor
  ArgumentCaptor<ClientHttpRequestDataEvent> eventArgCaptor;
  @LocalServerPort
  Integer serverPort;

  @Test
  void test_ok() {
    var uri = rootUri() + "/todos/1";
    var responseEntity = restClient
        .get().uri(uri)
        .retrieve().toEntity(Todo.class);
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    log.debug("responseEntity.body: {}", responseEntity.getBody());
    assertThat(responseEntity.getBody())
        .isNotNull();
  }

  @Test
  void test_restClient_not_found() {
    var uri = rootUri() + "/todos/999999";
    assertThatThrownBy(() -> restClient.get().uri(uri).retrieve().body(Todo.class))
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class)
        .isInstanceOf(NotFound.class)
        .satisfies(throwable -> {
          RestClientResponseException restClientResponseException = (RestClientResponseException) throwable;
          log.debug("responseBody: {}", restClientResponseException.getResponseBodyAsString());
        });
  }

  @Test
  void test_restClient_not_found_with_onStatus() {
    var uri = rootUri() + "/todos/999999";
    assertThatThrownBy(() ->
        restClient
            .get().uri(uri)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
              throw new IllegalArgumentException("Not found");
            })
            .body(Todo.class)
    )
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void test_restClient_not_found_with_exchange_handling_404() {
    var uri = rootUri() + "/todos/999999";
    var data = restClient
        .get().uri(uri)
        .exchange((request, response) -> {
          if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return null;
          }
          return new String(response.getBody().readAllBytes());
        });
    log.debug(data);
    assertThat(data).isNull();
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
