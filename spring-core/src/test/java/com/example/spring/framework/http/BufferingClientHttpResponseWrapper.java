package com.example.spring.framework.http;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.http.client.MockClientHttpResponse;

/**
 * Workaround for getting a response that is buffered for tests, without using package private class <code>org.springframework.http.client.BufferingClientHttpResponseWrapper</code>.
 * @see org.springframework.http.client.BufferingClientHttpResponseWrapper
 */
class BufferingClientHttpResponseWrapper extends MockClientHttpResponse {

  public BufferingClientHttpResponseWrapper(byte[] body, HttpStatusCode statusCode) {
    super(body, statusCode);
  }

  @Override
  public InputStream getBody() throws IOException {
    var body = super.getBody();
    body.reset();
    return body;
  }
}
