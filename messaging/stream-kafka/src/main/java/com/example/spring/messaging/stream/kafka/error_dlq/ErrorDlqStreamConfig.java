package com.example.spring.messaging.stream.kafka.error_dlq;

import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Configuration
@ConditionalOnProperty(value = "app.kafka-apps.error_dlq.enabled", havingValue = "true")
@Slf4j
public class ErrorDlqStreamConfig {

  @Bean
  @ConditionalOnProperty(value = "app.kafka-apps.error_dlq.producers.enabled", havingValue = "true")
  public Supplier<Flux<String>> errorDlqProducer() {
    return () ->
        Flux.range(1, 10)
            .map(value -> ((value % 2 == 0) ? "ok" : "error") + value)
            .log();
  }

  @Bean
  public Consumer<String> errorDlqConsumer(ErrorDlqConsumerBean errorDlqConsumerBean) {
    return errorDlqConsumerBean::consume;
  }

  @Bean
  public ErrorDlqConsumerBean errorDlqConsumerBean() {
    return new ErrorDlqConsumerBean();
  }

  public static class ErrorDlqConsumerBean {

    // trying with @Transaction, to make sure it also works on these scenarios
    @Transactional
    public void consume(String value) {
      log.info("errorDlqConsumer :: {}", value);
      if (value.contains("error")) {
        throw new UnsupportedOperationException("some error :: %s".formatted(value));
      }
    }
  }
}