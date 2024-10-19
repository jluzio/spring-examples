package com.example.spring.secure_service_client.service;

import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SecureServiceClient {

  private final RestClient client;

  public SecureServiceClient(RestClient.Builder restClientBuilder, RestClientSsl ssl) {
    this.client = restClientBuilder
        .baseUrl("https://localhost")
        .apply(ssl.fromBundle("client"))
        .build();
  }

  public String hello() {
    return client
        .get().uri("/hello")
        .retrieve()
        .body(String.class);
  }
}
