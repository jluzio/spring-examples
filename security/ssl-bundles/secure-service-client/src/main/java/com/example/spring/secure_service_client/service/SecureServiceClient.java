package com.example.spring.secure_service_client.service;

import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SecureServiceClient {

  private final RestTemplate restTemplate;

  public SecureServiceClient(RestTemplateBuilder restTemplateBuilder, SslBundles sslBundles) {
    this.restTemplate = restTemplateBuilder.setSslBundle(sslBundles.getBundle("client")).build();
  }

  public String hello() {
    return restTemplate.getForEntity("https://localhost/hello", String.class)
        .getBody();
  }
}
