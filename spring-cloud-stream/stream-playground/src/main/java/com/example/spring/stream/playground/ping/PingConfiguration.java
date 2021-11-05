package com.example.spring.stream.playground.ping;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PingConfiguration {

  @Bean
  public Supplier<String> ping() {
    return () -> {
      String pingMsg = "ping-%s".formatted(LocalDateTime.now());
      log.debug(pingMsg);
      return pingMsg;
    };
  }

  @Bean
  public Consumer<String> pong() {
    return args -> log.info("pong: %s".formatted(args));
  }

}
