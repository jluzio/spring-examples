package com.example.spring.stream.playground.string;

import com.github.javafaker.Faker;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
@Slf4j
public class StringStreamConfig {

  private final Faker faker = new Faker();


  @Bean
  @ConditionalOnProperty(value = "app.producers.randomNameSupplier", havingValue = "true")
  public Supplier<Flux<String>> randomNameSupplier() {
    return () -> Flux.interval(Duration.ofSeconds(3))
        .map(ignored -> faker.name().firstName());
  }

  @Bean
  public Function<String, String> uppercaseFunction() {
    return String::toUpperCase;
  }

  @Bean
  public Function<String, String> reverseFunction() {
    return StringUtils::reverse;
  }

  @Bean
  public Consumer<String> stringConsumer() {
    return value -> log.info("consumed string: %s".formatted(value));
  }

}
