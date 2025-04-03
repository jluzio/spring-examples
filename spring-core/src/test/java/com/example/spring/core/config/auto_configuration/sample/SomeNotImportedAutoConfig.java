package com.example.spring.core.config.auto_configuration.sample;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SomeNotImportedAutoConfig {

  @Bean
  String someNotImportedAutoConfigBean() {
    return "someNotImportedAutoConfigBean";
  }
}
