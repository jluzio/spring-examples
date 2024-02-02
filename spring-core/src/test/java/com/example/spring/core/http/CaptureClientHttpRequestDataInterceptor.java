package com.example.spring.core.http;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

@RequiredArgsConstructor
public class CaptureClientHttpRequestDataInterceptor implements ClientHttpRequestInterceptor {

  private final ApplicationEventPublisher eventPublisher;

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request,
      byte[] body,
      ClientHttpRequestExecution execution
  ) throws IOException {
    var requestDataBuilder = ClientHttpRequestData.builder()
        .request(request)
        .requestBody(body);
    try {
      var response = execution.execute(request, body);
      var responseBody = getResponseBody(response);

      requestDataBuilder
          .response(response)
          .responseStatus(response.getStatusCode())
          .responseBody(responseBody);

      return response;
    } catch (IOException | RuntimeException e) {
      var responseStatus = getHttpStatus(e);

      requestDataBuilder
          .responseException(e)
          .responseStatus(responseStatus);

      throw e;
    } finally {
      eventPublisher.publishEvent(
          new ClientHttpRequestDataEvent(this, requestDataBuilder.build()));
    }
  }

  private byte[] getResponseBody(ClientHttpResponse response) throws IOException {
    byte[] body = null;
    if (isResponseBuffered(response)) {
      try {
        InputStream bodyInput = response.getBody();
        body = bodyInput.readAllBytes();
      } catch (IOException e) {
        // Ignoring the exception. Response is buffered, so body must have no content.
      }
    }
    return body;
  }

  /**
   * @see org.springframework.http.client.BufferingClientHttpResponseWrapper
   */
  private boolean isResponseBuffered(ClientHttpResponse response) {
    return response.getClass().getName().contains("Buffer");
  }

  private HttpStatusCode getHttpStatus(Throwable e) {
    HttpStatusCode httpStatus = null;
    if (e instanceof HttpStatusCodeException hsce) {
      httpStatus = hsce.getStatusCode();
    } else {
      var responseStatus = e.getClass().getAnnotation(ResponseStatus.class);
      if (responseStatus != null) {
        httpStatus = responseStatus.code();
      }
    }
    return httpStatus;
  }
}
