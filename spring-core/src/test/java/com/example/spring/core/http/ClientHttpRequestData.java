package com.example.spring.core.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

@Value
@AllArgsConstructor
@Builder
public class ClientHttpRequestData {

  HttpRequest request;
  byte[] requestBody;

  ClientHttpResponse response;
  HttpStatusCode responseStatus;
  Throwable responseException;
  byte[] responseBody;

}
