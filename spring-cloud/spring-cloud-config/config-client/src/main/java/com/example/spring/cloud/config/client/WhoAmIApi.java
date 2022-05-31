package com.example.spring.cloud.config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RestController
public class WhoAmIApi {

  @Value("${user.role}")
  private String role;

  @GetMapping(
      value = "/whoami/{username}",
      produces = MediaType.TEXT_PLAIN_VALUE)
  public String whoami(@PathVariable("username") String username) {
    return "Hello! You're %s and you'll become a(n) %s...".formatted(username, role);
  }
}