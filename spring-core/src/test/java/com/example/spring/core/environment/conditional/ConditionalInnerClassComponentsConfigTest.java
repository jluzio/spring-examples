package com.example.spring.core.environment.conditional;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class ConditionalInnerClassComponentsConfigTest {

  @Import(ConditionalInnerClassComponentsConfig.class)
  static class Config {

  }

  @Configuration
  @ConditionalOnProperty(value = "conditionalInnerComponentsConfig", havingValue = "true")
  static class ConditionalInnerClassComponentsConfig {

    @RestController
    class DebugController {

      @GetMapping("/debug/hello")
      String hello() {
        return "hello";
      }
    }
  }

  @Test
  void enabledTest() {
    appCtxRunner(true).run(ctx -> {
      assertThat(ctx.getBeanNamesForType(ConditionalInnerClassComponentsConfig.DebugController.class))
          .isNotEmpty();
    });
  }

  @Test
  void disabledTest() {
    appCtxRunner(false).run(ctx -> {
      assertThat(ctx.getBeanNamesForType(ConditionalInnerClassComponentsConfig.DebugController.class))
          .isEmpty();
    });
  }

  ApplicationContextRunner appCtxRunner(boolean enabled) {
    return new ApplicationContextRunner()
        .withUserConfiguration(Config.class)
        .withPropertyValues(
            "conditionalInnerComponentsConfig=%s".formatted(enabled)
        );
  }

}