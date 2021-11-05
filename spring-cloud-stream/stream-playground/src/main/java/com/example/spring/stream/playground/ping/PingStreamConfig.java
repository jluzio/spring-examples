package com.example.spring.stream.playground.ping;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ping")
@Slf4j
public class PingStreamConfig {

  @Bean
  public Supplier<String> pingSupplier() {
    return () -> {
      String pingMsg = "ping-%s".formatted(LocalDateTime.now());
      log.debug(pingMsg);
      return pingMsg;
    };
  }

  @Bean
  public Consumer<String> pingConsumer() {
    return args -> log.info("pong: %s".formatted(args));
  }

}
