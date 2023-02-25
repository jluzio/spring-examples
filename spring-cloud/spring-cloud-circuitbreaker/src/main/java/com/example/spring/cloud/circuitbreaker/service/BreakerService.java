package com.example.spring.cloud.circuitbreaker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BreakerService {

  private final ReactiveResilience4JCircuitBreakerFactory cbFactory;

  public <T> Mono<T> slowApi(Mono<T> supplier, T fallback) {
    return supplier
        .transform(it -> cbFactory.create("slowApi")
            .run(it, throwable -> Mono.just(fallback)));
  }

  public <T> Mono<T> defaultApi(Mono<T> supplier, T fallback) {
    return supplier
        .transform(it -> cbFactory.create("defaultApi")
            .run(it, throwable -> Mono.just(fallback)));
  }
}
