package com.example.spring.secure_service_client.api;

import com.example.spring.secure_service_client.service.SecureServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClientController {

  private final SecureServiceClient client;

  @GetMapping("/hello")
  public String hello() {
    return client.hello();
  }
}
