package com.example.spring.framework.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
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
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
@RecordApplicationEvents
class CaptureClientHttpRequestDataInterceptorIT {

  @TestConfiguration
  @Import({CaptureClientHttpRequestDataInterceptor.class, LoggingEventListener.class,
      DataController.class})
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
          .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }
  }

  @Autowired
  RestClient restClient;
  @Autowired
  RestClient restClientWithExceptionHandlingInterceptor;
  @LocalServerPort
  int port;
  @Autowired
  ApplicationEvents applicationEvents;


  @Test
  void test_ok() {
    var apiUri = rootUri() + "/data/1";
    var responseEntity = getRetrieveEntity(restClient, apiUri);
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
    var apiUri = rootUri() + "/data/not-found";
    assertThatThrownBy(() -> getRetrieveEntity(restClient, apiUri))
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
  void test_error() {
    var apiUri = rootUri() + "/data/error";
    assertThatThrownBy(() -> getRetrieveEntity(restClient, apiUri))
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class)
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
    var apiUri = rootUri() + "/data/not-found";
    assertThatThrownBy(() -> getRetrieveEntity(restClientWithExceptionHandlingInterceptor, apiUri))
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

  private String rootUri() {
    return String.format("http://localhost:%s", port);
  }

  @RestController
  static class DataController {

    @GetMapping("/data/not-found")
    public ResponseEntity<String> dataNotFound() {
      return ResponseEntity.notFound().build();
    }

    @GetMapping("/data/error")
    public ResponseEntity<String> dataError() {
      return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
          .body("NOT_IMPLEMENTED");
    }

    @GetMapping("/data/{id}")
    public ResponseEntity<String> dataOk(@PathVariable Integer id) {
      return ResponseEntity.ok(String.format("Data-%s", id));
    }
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
