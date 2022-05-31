package com.example.spring.cloud.playground.function;

import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
@Slf4j
public class LangFunctions {

  @Bean
  // Imperative
  public Function<String, String> lowercase() {
    return String::toLowerCase;
  }

  @Bean
  public Function<String, String> uppercase() {
    return String::toUpperCase;
  }

  @Bean
  // Reactive
  public Function<Flux<String>, Flux<String>> uppercaseReactive() {
    return flux -> flux.map(String::toUpperCase);
  }

  @Bean
  public Function<String, String> reverse() {
    return StringUtils::reverse;
  }

}
