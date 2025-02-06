package com.example.spring.core.log;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

class StructuredLoggingTest {

  static class Config {

  }

  @Test
  void log_logstash() {
    assertThatNoException().isThrownBy(() -> test_logging("logstash"));
  }

  @Test
  void log_ecs() {
    assertThatNoException().isThrownBy(() -> test_logging("ecs"));
  }

  void test_logging(String format) {
    var formatProperty = "logging.structured.format.console=%s".formatted(format);
    try (var app = app()
        .properties(formatProperty)
        .run()
    ) {
      var log = LoggerFactory.getLogger(this.getClass());
      log.info("Log using '{}'", formatProperty);
    }
  }

  SpringApplicationBuilder app() {
    return new SpringApplicationBuilder(StructuredLoggingTest.Config.class)
        .web(WebApplicationType.NONE);
  }

}
