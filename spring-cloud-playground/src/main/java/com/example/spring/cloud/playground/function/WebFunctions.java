package com.example.spring.cloud.playground.function;

import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
public class WebFunctions {

  @Bean
  // Imperative
  public Function<String, String> lowercase() {
    return String::toLowerCase;
  }

  @Bean
  // Reactive
  public Function<Flux<String>, Flux<String>> uppercase() {
    return flux -> flux.map(String::toUpperCase);
  }

}
