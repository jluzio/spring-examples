package com.example.spring.framework.config.auto_configuration.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SomeImportedConfig {

  @Bean
  String someImportedConfigBean() {
    return "someImportedConfigBean";
  }
}
