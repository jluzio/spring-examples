package com.example.spring.framework.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@SpringBootTest
@Slf4j
@RecordApplicationEvents
class CaptureClientHttpRequestDataInterceptorTest {

  @Configuration
  @Import({CaptureClientHttpRequestDataInterceptor.class})
  static class Config {

  }

  @Autowired
  CaptureClientHttpRequestDataInterceptor interceptor;
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
  void test_status_ok() throws IOException {
    var request = mockClientHttpRequest("/data");
    var requestBody = "dummy";
    var execution = mockClientHttpRequestExecution(
        HttpStatus.OK.getReasonPhrase().getBytes(), HttpStatus.OK);
    var responseBody = HttpStatus.OK.getReasonPhrase();

    var response = interceptor.intercept(request, requestBody.getBytes(), execution);

    assertThat(response.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertResponseStringData(response)
        .isEqualTo(responseBody);

    var capturedEvent = getCapturedEvent();
    assertThat(capturedEvent)
        .isPresent()
        .get()
        .isNotNull()
        .satisfies(it -> log.debug("event: {}", it));

    var requestData = capturedEvent.get().getRequestData();
    assertThat(requestData)
        .satisfies(it -> assertThat(it.getResponseException()).isNull())
        .usingRecursiveComparison()
        .ignoringExpectedNullFields()
        .isEqualTo(ClientHttpRequestData.builder()
            .request(request)
            .requestTimestamp(instant1)
            .response(response)
            .responseStatus(response.getStatusCode())
            .responseTimestamp(instant2)
            .build());
    assertThat(requestData.getRequestBody())
        .asString()
        .satisfies(it -> log.debug("requestBody: {}", it))
        .isEqualTo(requestBody);
    assertThat(requestData.getResponseBody())
        .asString()
        .satisfies(it -> log.debug("responseBody: {}", it))
        .isEqualTo(responseBody);
  }

  @Test
  void test_status_error() throws IOException {
    var request = mockClientHttpRequest("/data");
    var requestBody = "dummy";
    var responseBody = HttpStatus.NOT_IMPLEMENTED.getReasonPhrase();
    var execution = mockClientHttpRequestExecution(
        responseBody.getBytes(), HttpStatus.NOT_IMPLEMENTED);

    var response = interceptor.intercept(request, requestBody.getBytes(), execution);

    assertThat(response.getStatusCode())
        .isEqualTo(HttpStatus.NOT_IMPLEMENTED);
    assertResponseStringData(response)
        .isEqualTo(responseBody);

    var capturedEvent = getCapturedEvent();
    assertThat(capturedEvent)
        .isPresent()
        .get()
        .isNotNull()
        .satisfies(it -> log.debug("event: {}", it));

    var requestData = capturedEvent.get().getRequestData();
    assertThat(requestData)
        .satisfies(it -> assertThat(it.getResponseException()).isNull())
        .usingRecursiveComparison()
        .ignoringExpectedNullFields()
        .isEqualTo(ClientHttpRequestData.builder()
            .request(request)
            .requestTimestamp(instant1)
            .response(response)
            .responseStatus(response.getStatusCode())
            .responseTimestamp(instant2)
            .build());
    assertThat(requestData.getRequestBody())
        .asString()
        .satisfies(it -> log.debug("requestBody: {}", it))
        .isEqualTo(requestBody);
    assertThat(requestData.getResponseBody())
        .asString()
        .satisfies(it -> log.debug("responseBody: {}", it))
        .isEqualTo(responseBody);
  }

  @Test
  void test_status_error_RestClientException() {
    var request = mockClientHttpRequest("/data");
    var requestBody = "dummy";
    var requestBodyBytes = requestBody.getBytes();
    var execution = exceptionClientHttpRequestExecution(
        HttpClientErrorException.create(
            HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null));

    assertThatThrownBy(() -> interceptor.intercept(request, requestBodyBytes, execution))
        .isInstanceOf(RestClientException.class)
        .isInstanceOf(RestClientResponseException.class)
        .isInstanceOf(NotFound.class)
        .satisfies(_ -> {
          var capturedEvent = getCapturedEvent();
          assertThat(capturedEvent)
              .isPresent()
              .get()
              .isNotNull()
              .satisfies(it -> log.debug("event: {}", it));

          var requestData = capturedEvent.get().getRequestData();
          assertThat(requestData)
              .usingRecursiveComparison()
              .ignoringExpectedNullFields()
              .isEqualTo(ClientHttpRequestData.builder()
                  .request(request)
                  .requestTimestamp(instant1)
                  .responseTimestamp(instant2)
                  .build());
          assertThat(requestData.getRequestBody())
              .asString()
              .satisfies(it -> log.debug("requestBody: {}", it))
              .isEqualTo(requestBody);
          assertThat(requestData)
              .extracting(ClientHttpRequestData::getResponse,
                  ClientHttpRequestData::getResponseBody)
              .allSatisfy(it -> assertThat(it).isNull());
          assertThat(requestData.getResponseException())
              .isInstanceOf(NotFound.class);
        });
  }

  @Test
  void test_status_error_IOException() {
    var request = mockClientHttpRequest("/data");
    var requestBody = "dummy";
    var requestBodyBytes = requestBody.getBytes();
    var execution = exceptionClientHttpRequestExecution(
        new IOException(HttpStatus.NOT_FOUND.getReasonPhrase()));

    assertThatThrownBy(() -> interceptor.intercept(request, requestBodyBytes, execution))
        .isInstanceOf(IOException.class)
        .satisfies(_ -> {
          var capturedEvent = getCapturedEvent();
          assertThat(capturedEvent)
              .isPresent()
              .get()
              .isNotNull()
              .satisfies(it -> log.debug("event: {}", it));

          var requestData = capturedEvent.get().getRequestData();
          assertThat(requestData)
              .usingRecursiveComparison()
              .ignoringExpectedNullFields()
              .isEqualTo(ClientHttpRequestData.builder()
                  .request(request)
                  .requestTimestamp(instant1)
                  .responseTimestamp(instant2)
                  .build());
          assertThat(requestData.getRequestBody())
              .asString()
              .satisfies(it -> log.debug("requestBody: {}", it))
              .isEqualTo(requestBody);
          assertThat(requestData)
              .extracting(ClientHttpRequestData::getResponse,
                  ClientHttpRequestData::getResponseBody)
              .allSatisfy(it -> assertThat(it).isNull());
          assertThat(requestData.getResponseException())
              .isInstanceOf(IOException.class);
        });
  }

  private Optional<ClientHttpRequestDataEvent> getCapturedEvent() {
    return applicationEvents.stream(ClientHttpRequestDataEvent.class).findFirst();
  }

  MockClientHttpRequest mockClientHttpRequest(String uri) {
    return new MockClientHttpRequest(HttpMethod.GET, uri);
  }

  AbstractStringAssert<?> assertResponseStringData(ClientHttpResponse response) throws IOException {
    return assertThat(response.getBody())
        .isNotNull()
        .asString(StandardCharsets.UTF_8);
  }

  ClientHttpRequestExecution mockClientHttpRequestExecution(byte[] data, HttpStatus httpStatus) {
    return (request, body) -> new BufferingClientHttpResponseWrapper(data, httpStatus);
  }

  ClientHttpRequestExecution exceptionClientHttpRequestExecution(IOException throwable) {
    return (request, body) -> {
      throw throwable;
    };
  }

  ClientHttpRequestExecution exceptionClientHttpRequestExecution(RuntimeException throwable) {
    return (request, body) -> {
      throw throwable;
    };
  }

}
