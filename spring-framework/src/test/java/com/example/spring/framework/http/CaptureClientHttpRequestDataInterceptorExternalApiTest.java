package com.example.spring.framework.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@SpringBootTest
@Slf4j
@RecordApplicationEvents
class CaptureClientHttpRequestDataInterceptorExternalApiTest {

  public static final String ROOT_URI = "https://jsonplaceholder.typicode.com";

  @Configuration
  @Import({CaptureClientHttpRequestDataInterceptor.class, LoggingEventListener.class})
  static class Config {

    @Bean
    RestClient restClient(Collection<ClientHttpRequestInterceptor> interceptors) {
      return baseRestClientBuilder()
          .requestInterceptors(it -> it.addAll(interceptors))
          .build();
    }

    @Bean
    RestClient restClientWithExceptionHandlingInterceptor(
        Collection<ClientHttpRequestInterceptor> interceptors) {
      return baseRestClientBuilder()
          .requestInterceptors(it -> it.addAll(interceptors))
          .requestInterceptor(new NotFoundInterceptor())
          .build();
    }

    RestClient.Builder baseRestClientBuilder() {
      return RestClient.builder()
          .baseUrl(ROOT_URI)
          .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }
  }

  @Autowired
  RestClient restClient;
  @Autowired
  RestClient restClientWithExceptionHandlingInterceptor;
  @Autowired
  ApplicationEvents applicationEvents;
  @MockitoBean
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
    var responseEntity = getRetrieveEntity(restClient, "/todos/1");
    log.debug("responseEntity: {}", responseEntity);
    assertThat(responseEntity.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    log.debug("responseEntity.body: {}", responseEntity.getBody());
    assertThat(responseEntity.getBody())
        .isNotEmpty();

    var capturedEvent = getCapturedEvent();
    assertThat(capturedEvent)
        .isPresent()
        .get()
        .isNotNull()
        .satisfies(it -> log.debug("event: {}", it));

    var requestData = capturedEvent.get().getRequestData();
    var responsePayload = requestData.getResponseBody();
    var responsePayloadString = new String(responsePayload);
    log.debug("responsePayloadString: {}", responsePayloadString);
    assertThat(responsePayloadString)
        .isNotEmpty();
  }

  @Test
  void test_not_found() {
    assertThatThrownBy(() -> getRetrieveEntity(restClient, "/todos/999999")
    )
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class)
        .isInstanceOf(NotFound.class)
        .satisfies(throwable -> {
          RestClientResponseException restClientResponseException = (RestClientResponseException) throwable;
          log.debug("responseBody: {}", restClientResponseException.getResponseBodyAsString());

          var capturedEvent = getCapturedEvent();
          assertThat(capturedEvent)
              .isPresent()
              .get()
              .isNotNull()
              .satisfies(it -> log.debug("event: {}", it));

          var requestData = capturedEvent.get().getRequestData();
          assertThat(requestData.getResponseException())
              .isNull();
        });
  }

  @Test
  void test_not_found_with_exception_interceptor() {
    assertThatThrownBy(() -> getRetrieveEntity(restClientWithExceptionHandlingInterceptor, "/todos/999999"))
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class)
        .isInstanceOf(NotFound.class)
        .satisfies(throwable -> {
          RestClientResponseException restClientResponseException = (RestClientResponseException) throwable;
          log.debug("responseBody: {}", restClientResponseException.getResponseBodyAsString());

          var capturedEvent = getCapturedEvent();
          assertThat(capturedEvent)
              .isPresent()
              .get()
              .isNotNull()
              .satisfies(it -> log.debug("event: {}", it));

          var requestData = capturedEvent.get().getRequestData();
          assertThat(requestData.getResponseException())
              .isNotNull()
              .isInstanceOf(NotFound.class);
        });
  }

  private ResponseEntity<String> getRetrieveEntity(RestClient restClient, String apiUri) {
    return restClient.get().uri(apiUri).retrieve().toEntity(String.class);
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

  private Optional<ClientHttpRequestDataEvent> getCapturedEvent() {
    return applicationEvents.stream(ClientHttpRequestDataEvent.class).findFirst();
  }

}
