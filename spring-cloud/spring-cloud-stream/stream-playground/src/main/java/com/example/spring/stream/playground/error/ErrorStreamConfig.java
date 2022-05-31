package com.example.spring.stream.playground.error;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ErrorMessage;

@Configuration
@Profile("error")
@Slf4j
public class ErrorStreamConfig {
  private final AtomicInteger counter = new AtomicInteger(0);

  @Bean
  public Supplier<String> unreliableDataSupplier() {
    return () -> {
      int value = counter.getAndIncrement();
      if (value % 2 == 0) {
        return String.valueOf(value);
      } else {
        throw new IllegalArgumentException("not even error");
      }
    };
  }

  @Bean
  public Consumer<String> unreliableDataConsumer() {
    return data -> log.info("data: {}", data);
  }

  @ServiceActivator(
      // Specify which channel exception to handle through a specific format
//      inputChannel = "unreliable-data.watchlist.errors"
//      inputChannel = "unreliable-data.errors"
      inputChannel = "errorChannel"
  )
  public void handleError(ErrorMessage errorMessage) {
    // Getting exception objects
    Throwable errorMessagePayload = errorMessage.getPayload();
    log.error("exception occurred", errorMessagePayload);

    // Get message body
    Message<?> originalMessage = errorMessage.getOriginalMessage();
    if (originalMessage != null) {
      log.error("Message Body: {}", originalMessage.getPayload());
    } else {
      log.error("The message body is empty");
    }
  }

}
