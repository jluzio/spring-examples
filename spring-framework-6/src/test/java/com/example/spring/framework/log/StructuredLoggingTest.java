package com.example.spring.framework.log;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

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
    appCtxRunner()
        .withPropertyValues(formatProperty)
        .run(ctx -> {
          var log = LoggerFactory.getLogger(this.getClass());
          log.info("Log using '{}'", formatProperty);
        });
  }

  ApplicationContextRunner appCtxRunner() {
    return new ApplicationContextRunner()
        .withUserConfiguration(StructuredLoggingTest.Config.class);
  }

}
