package com.example.spring.core.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@SpringBootTest
@Slf4j
class CaptureClientHttpRequestDataInterceptorTest {

  @Configuration
  @Import({CaptureClientHttpRequestDataInterceptor.class, NopEventListener.class})
  static class Config {

  }

  @Autowired
  CaptureClientHttpRequestDataInterceptor interceptor;
  @Captor
  ArgumentCaptor<ClientHttpRequestDataEvent> eventArgCaptor;
  @SpyBean
  NopEventListener eventListener;
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

    verify(eventListener).listen(eventArgCaptor.capture());
    assertThat(eventArgCaptor.getValue())
        .isNotNull()
        .satisfies(it -> log.debug("event: {}", it));

    var requestData = eventArgCaptor.getValue().getRequestData();
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
    var responseBody = HttpStatus.I_AM_A_TEAPOT.getReasonPhrase();
    var execution = mockClientHttpRequestExecution(
        responseBody.getBytes(), HttpStatus.I_AM_A_TEAPOT);

    var response = interceptor.intercept(request, requestBody.getBytes(), execution);

    assertThat(response.getStatusCode())
        .isEqualTo(HttpStatus.I_AM_A_TEAPOT);
    assertResponseStringData(response)
        .isEqualTo(responseBody);

    verify(eventListener).listen(eventArgCaptor.capture());
    assertThat(eventArgCaptor.getValue())
        .isNotNull()
        .satisfies(it -> log.debug("event: {}", it));

    var requestData = eventArgCaptor.getValue().getRequestData();
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
  void test_status_error_RestClientException() throws IOException {
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
        .satisfies(throwable -> {
          verify(eventListener).listen(eventArgCaptor.capture());
          assertThat(eventArgCaptor.getValue())
              .isNotNull()
              .satisfies(it -> log.debug("event: {}", it));

          var requestData = eventArgCaptor.getValue().getRequestData();
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
  void test_status_error_IOException() throws IOException {
    var request = mockClientHttpRequest("/data");
    var requestBody = "dummy";
    var requestBodyBytes = requestBody.getBytes();
    var execution = exceptionClientHttpRequestExecution(
        new IOException(HttpStatus.NOT_FOUND.getReasonPhrase()));

    assertThatThrownBy(() -> interceptor.intercept(request, requestBodyBytes, execution))
        .isInstanceOf(IOException.class)
        .satisfies(throwable -> {
          verify(eventListener).listen(eventArgCaptor.capture());
          assertThat(eventArgCaptor.getValue())
              .isNotNull()
              .satisfies(it -> log.debug("event: {}", it));

          var requestData = eventArgCaptor.getValue().getRequestData();
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

  static class NopEventListener {

    @EventListener
    public void listen(ClientHttpRequestDataEvent event) {
      // nop
    }
  }
}
