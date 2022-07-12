package com.example.spring.core.config.auto_configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleAutoConfiguration {

  @Bean
  String autoConfiguredBean() {
    return "autoConfiguredBeanValue";
  }

  @Configuration
  static class IncludedConfig {

    @Bean
    String anotherAutoConfiguredBean() {
      return "anotherAutoConfiguredBeanValue";
    }
  }
}
