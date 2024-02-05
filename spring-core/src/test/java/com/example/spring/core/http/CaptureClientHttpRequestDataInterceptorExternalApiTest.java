package com.example.spring.core.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@Slf4j
class CaptureClientHttpRequestDataInterceptorExternalApiTest {

  public static final String ROOT_URI = "https://jsonplaceholder.typicode.com";

  @Configuration
  @Import({CaptureClientHttpRequestDataInterceptor.class, LoggingEventListener.class})
  static class Config {

    @Bean
    RestTemplate restTemplate(Collection<ClientHttpRequestInterceptor> interceptors) {
      return baseRestTemplateBuilder()
          .additionalInterceptors(interceptors)
          .build();
    }

    @Bean
    RestTemplate restTemplateWithExceptionHandlingInterceptor(
        Collection<ClientHttpRequestInterceptor> interceptors) {
      return baseRestTemplateBuilder()
          .additionalInterceptors(interceptors)
          .additionalInterceptors(new NotFoundInterceptor())
          .build();
    }

    RestTemplateBuilder baseRestTemplateBuilder() {
      return new RestTemplateBuilder()
          .rootUri(ROOT_URI)
          .requestFactory(
              () -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }
  }

  @Autowired
  @Qualifier("restTemplate")
  RestTemplate restTemplate;
  @Autowired
  @Qualifier("restTemplateWithExceptionHandlingInterceptor")
  RestTemplate restTemplateWithExceptionHandlingInterceptor;
  @SpyBean
  LoggingEventListener eventListener;
  @Captor
  ArgumentCaptor<ClientHttpRequestDataEvent> eventArgCaptor;
  @MockBean
  Clock clock;
  Instant instant1 = Instant.parse("2020-01-02T03:04:05Z");
  Instant instant2 = Instant.parse("2020-01-02T03:04:06Z");

  @BeforeEach
  void setup() {
    when(clock.instant())
        .thenReturn(instant1, instant2);
  }


  @Test
  void test_ok() {
    var responseEntity = restTemplate.getForEntity("/todos/1", String.class);
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    log.debug("responseEntity.body: {}", responseEntity.getBody());
    assertThat(responseEntity.getBody())
        .isNotEmpty();

    verify(eventListener).receiveEvent(eventArgCaptor.capture());
    assertThat(eventArgCaptor.getValue())
        .isNotNull()
        .satisfies(it -> log.debug("event: {}", it));

    var requestData = eventArgCaptor.getValue().getRequestData();
    var responsePayload = requestData.getResponseBody();
    var responsePayloadString = new String(responsePayload);
    log.debug("responsePayloadString: {}", responsePayloadString);
    assertThat(responsePayloadString)
        .isNotEmpty();
  }

  @Test
  void test_not_found() {
    assertThatThrownBy(() -> restTemplate.getForEntity(
        "/todos/999999", String.class)
    )
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class)
        .isInstanceOf(NotFound.class)
        .satisfies(throwable -> {
          RestClientResponseException restClientResponseException = (RestClientResponseException) throwable;
          log.debug("responseBody: {}", restClientResponseException.getResponseBodyAsString());

          verify(eventListener).receiveEvent(eventArgCaptor.capture());
          assertThat(eventArgCaptor.getValue())
              .isNotNull()
              .satisfies(it -> log.debug("event: {}", it));

          var requestData = eventArgCaptor.getValue().getRequestData();
          assertThat(requestData.getResponseException())
              .isNull();
        });
  }

  @Test
  void test_not_found_with_exception_interceptor() {
    assertThatThrownBy(() -> restTemplateWithExceptionHandlingInterceptor.getForEntity(
        "/todos/999999", String.class))
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class)
        .isInstanceOf(NotFound.class)
        .satisfies(throwable -> {
          RestClientResponseException restClientResponseException = (RestClientResponseException) throwable;
          log.debug("responseBody: {}", restClientResponseException.getResponseBodyAsString());

          verify(eventListener).receiveEvent(eventArgCaptor.capture());
          assertThat(eventArgCaptor.getValue())
              .isNotNull()
              .satisfies(it -> log.debug("event: {}", it));

          var requestData = eventArgCaptor.getValue().getRequestData();
          assertThat(requestData.getResponseException())
              .isNotNull()
              .isInstanceOf(NotFound.class);
        });
  }

  static class LoggingEventListener {

    @EventListener
    public void receiveEvent(ClientHttpRequestDataEvent event) {
      log.debug("{}", event);
    }
  }

  static class NotFoundInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution) throws IOException {
      var response = execution.execute(request, body);
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw HttpClientErrorException.create(response.getStatusCode(), "Not Found", null, null,
            null);
      } else {
        return response;
      }
    }
  }
}
