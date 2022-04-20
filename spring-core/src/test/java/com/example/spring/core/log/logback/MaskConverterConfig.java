package com.example.spring.core.log.logback;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaskConverterConfig {

  @Bean
  @ConfigurationProperties(prefix = "app.log.mask-converter")
  MaskConverter maskConverter() {
    return new MaskConverter();
  }

}
