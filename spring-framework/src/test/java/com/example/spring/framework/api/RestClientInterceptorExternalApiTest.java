package com.example.spring.framework.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@SpringBootTest
@Slf4j
class RestClientInterceptorExternalApiTest {

  public static final String ROOT_URI = "https://jsonplaceholder.typicode.com";

  @Configuration
  static class Config {

    @Bean
    RestClient restClient(
        Collection<ClientHttpRequestInterceptor> interceptors
    ) {
      // not reusing RestClient.Builder, since it would share interceptors
      return RestClient.builder()
          .baseUrl(ROOT_URI)
          .requestInterceptors(clientHttpRequestInterceptors ->
              clientHttpRequestInterceptors.addAll(interceptors))
          .build();
    }

    @Bean
    RestClient reversedOrderRestClient(
        Collection<ClientHttpRequestInterceptor> interceptors
    ) {
      var reversedInterceptors = List.copyOf(interceptors).reversed();
      // not reusing RestClient.Builder, since it would share interceptors
      return RestClient.builder()
          .baseUrl(ROOT_URI)
          // since the bean instances don't have an annotation for order, they won't be reordered by RestClient
          .requestInterceptors(clientHttpRequestInterceptors ->
              clientHttpRequestInterceptors.addAll(reversedInterceptors))
          .build();
    }

    @Bean
    @Order(1)
    ClientHttpRequestInterceptor interceptor1() {
      return new ErrorInterceptor(
          request -> request.getMethod().equals(HttpMethod.GET),
          HttpStatusCode::isError,
          ignored -> new IllegalArgumentException("GENERIC ERROR")
      );
    }

    @Bean
    @Order(2)
    ClientHttpRequestInterceptor interceptor2() {
      return new ErrorInterceptor(
          request -> request.getMethod().equals(HttpMethod.GET),
          HttpStatus.NOT_FOUND::equals,
          ignored -> new NoSuchElementException("NOT_FOUND")
      );
    }

    @Bean
    @Order(3)
    ClientHttpRequestInterceptor interceptor3() {
      return new LogInterceptor();
    }
  }

  @Autowired
  RestClient restClient;
  @Autowired
  RestClient reversedOrderRestClient;

  @Test
  void test_ok() {
    var responseEntity = getRetrieveEntity(restClient, "/todos/1");
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.OK);
  }

  @Test
  void test_not_found() {
    assertThatThrownBy(() -> getRetrieveEntity(restClient, "/todos/999999"))
        .isInstanceOf(NoSuchElementException.class);
    assertThatThrownBy(() -> getRetrieveEntity(reversedOrderRestClient, "/todos/999999"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void test_other_httpMethod_error() {
    var retrieve = restClient.method(HttpMethod.TRACE)
        .uri("/todos/999999")
        .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML.toString())
        .retrieve();
    assertThatThrownBy(() -> retrieve.toEntity(String.class)
    )
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class);
  }

  private ResponseEntity<String> getRetrieveEntity(RestClient restClient, String apiUri) {
    return restClient.get().uri(apiUri).retrieve().toEntity(String.class);
  }

  @RequiredArgsConstructor
  static class ErrorInterceptor implements ClientHttpRequestInterceptor {

    private final Predicate<HttpRequest> httpRequestPredicate;
    private final Predicate<HttpStatusCode> statusCodePredicate;
    private final Function<ClientHttpResponse, RuntimeException> throwableSupplier;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution) throws IOException {
      var clientHttpResponse = execution.execute(request, body);
      if (httpRequestPredicate.test(request)
          && statusCodePredicate.test(clientHttpResponse.getStatusCode())) {
        throw throwableSupplier.apply(clientHttpResponse);
      }
      return clientHttpResponse;
    }
  }

  static class LogInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution) throws IOException {
      var clientHttpResponse = execution.execute(request, body);
      log.info("Response :: {}", clientHttpResponse.getStatusCode());
      return clientHttpResponse;
    }
  }

}
