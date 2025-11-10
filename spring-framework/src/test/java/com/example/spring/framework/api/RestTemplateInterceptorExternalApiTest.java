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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@Slf4j
class RestTemplateInterceptorExternalApiTest {

  public static final String ROOT_URI = "https://jsonplaceholder.typicode.com";

  @Configuration
  static class Config {

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
      return new RestTemplateBuilder()
          .rootUri(ROOT_URI);
    }

    @Bean
    RestTemplate restTemplate(
        RestTemplateBuilder builder,
        Collection<ClientHttpRequestInterceptor> interceptors
    ) {
      return builder
          // the order in the list is is the order of application, otherwise the bean class should have an @Order or similar
          .additionalInterceptors(interceptors)
          .build();
    }

    @Bean
    RestTemplate reversedOrderRestTemplate(
        RestTemplateBuilder builder,
        Collection<ClientHttpRequestInterceptor> interceptors
    ) {
      var reversedInterceptors = List.copyOf(interceptors).reversed();
      return builder
          // since the bean instances don't have an annotation for order, they won't be reordered by RestTemplate
          .additionalInterceptors(reversedInterceptors)
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
  RestTemplate restTemplate;
  @Autowired
  RestTemplate reversedOrderRestTemplate;

  @Test
  void test_ok() {
    var responseEntity = restTemplate.getForEntity("/todos/1", String.class);
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.OK);
  }

  @Test
  void test_not_found() {
    assertThatThrownBy(() -> restTemplate.getForEntity("/todos/999999", String.class))
        .isInstanceOf(NoSuchElementException.class);
    assertThatThrownBy(() -> reversedOrderRestTemplate.getForEntity("/todos/999999", String.class))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void test_other_httpMethod_error() {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setAccept(List.of(MediaType.TEXT_XML));
    log.debug("httpHeaders: {}", httpHeaders);

    assertThatThrownBy(() -> restTemplate.exchange(
        "/todos/999999",
        HttpMethod.TRACE,
        new HttpEntity<>(httpHeaders),
        String.class)
    )
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class);
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
