package com.example.spring.messaging.kafka.fizzbuzz;

import java.time.Duration;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
@Slf4j
public class FizzBuzzStreamConfig {

  Random random = new Random();

  @Bean
  @ConditionalOnProperty(value = "app.producers.fizzBuzzProducer", havingValue = "true")
  public Supplier<Flux<Integer>> fizzBuzzProducer(){
    return () ->
        Flux.interval(Duration.ofSeconds(5))
            .map(value -> random.nextInt(1000 - 1) + 1)
            .log();
  }

  @Bean
  public Function<Flux<Integer>, Flux<String>> fizzBuzzProcessor(){
    return longFlux -> longFlux
        .map(this::evaluateFizzBuzz)
        .log();
  }

  @Bean
  public Consumer<String> fizzBuzzConsumer(){
    return value -> log.info("Consumer Received : " + value);
  }

  private String evaluateFizzBuzz(Integer value) {
    if (value % 15 == 0) {
      return "FizzBuzz";
    } else if (value % 5 == 0) {
      return "Buzz";
    } else if (value % 3 == 0) {
      return "Fizz";
    } else {
      return String.valueOf(value);
    }
  }

}