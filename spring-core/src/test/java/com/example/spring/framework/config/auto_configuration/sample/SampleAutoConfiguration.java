package com.example.spring.framework.config.auto_configuration.sample;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(SomeImportedConfig.class)
public class SampleAutoConfiguration {

  @Bean
  String autoConfiguredBean() {
    return "autoConfiguredBean";
  }

  @Configuration
  static class InnerConfig {

    @Bean
    String innerConfigBean() {
      return "innerConfigBean";
    }
  }
}
