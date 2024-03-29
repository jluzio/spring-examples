package com.example.spring.cloud.playground.function;

import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
@Slf4j
public class UtilFunctions {

  @Bean
  public Consumer<Object> devNull() {
    return log();
  }

  @Bean
  public Consumer<Object> log() {
    return value -> log.info("log :: {}", value);
  }

  @Bean
  public <T> Function<T, T> logAndReturn() {
    return value -> {
      log.info("log :: {}", value);
      return value;
    };
  }

  @Bean
  public <T> Function<Message<T>, Message<T>> enrichMessage() {
    return message -> MessageBuilder.fromMessage(message)
        .setHeader("foo", "bar")
        .build();
  }

}
