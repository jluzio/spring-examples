package com.example.spring.framework.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.Exceptions;

@SpringBootTest
@Slf4j
class WebTestClientFilterTest {

  @Configuration
  static class Config {

    @Bean
    WebTestClient webTestClient() {
      var errorFilter1 = errorFilter(
          HttpStatusCode::isError,
          ignored -> new IllegalArgumentException("Error"));
      var errorFilter2 = errorFilter(
          HttpStatus.I_AM_A_TEAPOT::equals,
          ignored -> new UnsupportedOperationException("I_AM_A_TEAPOT"));
      var logFilter = logFilter();

      return WebTestClient
          .bindToController(new ExceptionController())
          .configureClient()
          .filter(errorFilter1)
          .filter(errorFilter2)
          .filter(logFilter)
          .build();
    }

    @RestController
    static class ExceptionController {

      @GetMapping(path = "/exception")
      public String test() {
        throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT);
      }
    }

    ExchangeFilterFunction errorFilter(
        Predicate<HttpStatusCode> statusCodePredicate,
        Function<ClientResponse, Throwable> throwableSupplier
    ) {
      return (request, next) ->
          next
              .exchange(request)
              .map(response -> {
                if (statusCodePredicate.test(response.statusCode())) {
                  throw Exceptions.propagate(throwableSupplier.apply(response));
                }
                return response;
              });
    }

    ExchangeFilterFunction logFilter() {
      return (request, next) ->
          next
              .exchange(request)
              .doOnNext(response -> {
                log.debug("Response :: {}", response.statusCode());
              });
    }
  }

  // Spring Boot will create a `WebTestClient` for you,
  // already configure and ready to issue requests against "localhost:RANDOM_PORT"
  @Autowired
  WebTestClient webTestClient;

  @Test
  void test() {
    var requestSpec = webTestClient
        .get().uri("/exception");
    assertThatThrownBy(requestSpec::exchange)
        .isInstanceOf(UnsupportedOperationException.class);
  }

}
