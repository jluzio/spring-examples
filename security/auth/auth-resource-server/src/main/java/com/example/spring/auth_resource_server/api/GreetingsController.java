package com.example.spring.auth_resource_server.api;

import com.example.spring.auth_resource_server.service.GreetingsService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GreetingsController {

  private final GreetingsService greetingsService;

  @GetMapping("/greetings")
  public Map<String, String> greet(
      @AuthenticationPrincipal Jwt jwt
    ) {
    log.info("jwt.subject: {}", jwt.getSubject());
    return greetingsService.greet();
  }

}
