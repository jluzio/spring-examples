package com.example.spring.cloud.playground.resilience4j;

import com.example.spring.cloud.playground.feign.JsonPlaceholderApi;
import com.example.spring.cloud.playground.feign.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resilience4j")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  public static final String CIRCUIT_BREAKER_NAME = "user";
  private final JsonPlaceholderApi jsonPlaceholderApi;
  private final Random random = new Random();

  @GetMapping("/users/{id}/success")
  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
  public User getUserSuccess(@PathVariable String id) {
    return jsonPlaceholderApi.user(id);
  }

  @GetMapping("/users/{id}/failure")
  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
  public User getUserFailure(@PathVariable String id) {
    throw new UnsupportedOperationException("In Error mode");
  }

  @GetMapping("/users/{id}/fallback")
  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getUserFallback")
  public User getUserFallback(@PathVariable String id) {
    return getUserFailure(id);
  }

  private User getUserFallback(String id, Throwable throwable) {
    return User.builder()
        .id(0)
        .username("johndoe")
        .email("johndoe@mail.org")
        .build();
  }

  @GetMapping("/users/{id}/retry")
  @Retry(name = "userRetry")
  public User getUserRetry(@PathVariable String id) {
    log.debug("getUserRetry: {}", id);
    return random.nextBoolean()
        ? getUserSuccess(id)
        : getUserFailure(id);
  }

}
