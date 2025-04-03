package com.example.spring.core.config.auto_configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
class AutoConfigurationTest {

  @Configuration
  @EnableAutoConfiguration
  static class Config {

  }

  @Autowired
  ApplicationContext context;

  @Test
  void test() {
    assertThat(context.getBean("autoConfiguredBean"))
        .isEqualTo("autoConfiguredBean");
    assertThat(context.getBean("innerBean"))
        .isEqualTo("innerBean");
    assertThat(context.getBean("someImportedBean"))
        .isEqualTo("someImportedBean");
    assertThat(context.containsBean("someNotImportedBean"))
        .isFalse();
  }
}
