package com.example.spring.batch.playground.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
class ConditionalOnAutoConfigureAppTest {

  public static final String BEAN_ID = "conditionalBean";
  public static final String PROPERTY = ConditionalOnAutoConfigureApp.PROPERTY;

  @Configuration
  @ConditionalOnAutoConfigureApp
  static class Config {

    @Bean(BEAN_ID)
    String conditionalBean() {
      return "42";
    }
  }

  @Test
  void test() {
    System.setProperty(PROPERTY, "true");
    try (var app = newApp(true).run()) {
      var property = app.getEnvironment().getProperty(PROPERTY);
      assertThat(property)
          .isNotNull();

      assertThat(app.containsBean(BEAN_ID))
          .isTrue();
    }

    System.setProperty(PROPERTY, "false");
    try (var app = newApp(false).run()) {
      var property = app.getEnvironment().getProperty(PROPERTY);
      assertThat(property)
          .isNotNull();

      assertThat(app.containsBean(BEAN_ID))
          .isFalse();
    }
  }

  SpringApplicationBuilder newApp(boolean defaultJobs) {
    return new SpringApplicationBuilder(ConditionalOnAutoConfigureAppTest.Config.class)
        .properties(Map.of(PROPERTY, defaultJobs));
  }
}
