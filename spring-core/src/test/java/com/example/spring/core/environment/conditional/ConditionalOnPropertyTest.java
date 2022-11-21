package com.example.spring.core.environment.conditional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

@Slf4j
class ConditionalOnPropertyTest {

  @Configuration
  static class Config {

    @Bean
    @ConditionalOnProperty(value = "config.stringBean1", havingValue = "true")
    String stringBean1() {
      return "stringBean1";
    }

  }

  @Test
  void test_not_defined() {
    runner().run(context -> {
      assertThatThrownBy(() -> context.getBean("stringBean1"))
          .isInstanceOf(NoSuchBeanDefinitionException.class);
    });
  }

  @Test
  void test_defined_property() {
    runner()
        .withPropertyValues("config.stringBean1=true")
        .run(context -> {
      assertThatNoException()
          .isThrownBy(() -> context.getBean("stringBean1"));
    });
  }

  @Test
  void test_defined_env() throws Exception {
    EnvironmentVariables envVars = new EnvironmentVariables()
        .set("config.stringBean1", "true");
    envVars.execute(() -> {
      runner()
          .run(context -> {
            assertThatNoException()
                .isThrownBy(() -> context.getBean("stringBean1"));
          });
    });
  }

  @Test
  void test_defined_env_screaming_case() throws Exception {
    EnvironmentVariables envVars = new EnvironmentVariables()
        .set("CONFIG_STRINGBEAN1", "true");
    envVars.execute(() -> {
      runner()
          .run(context -> {
            assertThatNoException()
                .isThrownBy(() -> context.getBean("stringBean1"));
          });
    });
  }

  private ApplicationContextRunner runner() {
    var runner = new ApplicationContextRunner()
        .withUserConfiguration(Config.class);
    return runner;
  }

}
