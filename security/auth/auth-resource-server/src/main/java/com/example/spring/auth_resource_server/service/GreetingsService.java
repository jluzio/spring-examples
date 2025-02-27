package com.example.spring.auth_resource_server.service;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GreetingsService {

  @PreAuthorize("hasAuthority('SCOPE_message.read')")
  public Map<String, String> greet() {
    var jwt = (Jwt) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
    var username = jwt.getSubject();
    return Map.of(
        "message", "Hello %s".formatted(username)
    );
  }

}
