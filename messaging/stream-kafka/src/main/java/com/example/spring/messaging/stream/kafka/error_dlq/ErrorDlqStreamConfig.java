package com.example.spring.messaging.stream.kafka.error_dlq;

import java.time.Duration;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
@ConditionalOnProperty(value = "app.kafka-apps.error_dlq.enabled", havingValue = "true")
@Slf4j
public class ErrorDlqStreamConfig {

  Random random = new Random();

  @Bean
  @ConditionalOnProperty(value = "app.kafka-apps.error_dlq.producers.enabled", havingValue = "true")
  public Supplier<Flux<Integer>> errorDlqProducer(){
    return () ->
        Flux.interval(Duration.ofSeconds(5))
            .map(value -> random.nextInt(1000 - 1) + 1)
            .log();
  }

  @Bean
  public Consumer<String> errorDlqConsumer(){
    return value -> {
      log.info("errorDlqConsumer :: {}", value);
      throw new UnsupportedOperationException("some error :: %s".formatted(value));
    };
  }
}