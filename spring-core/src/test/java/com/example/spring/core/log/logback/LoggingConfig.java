package com.example.spring.core.log.logback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

  @Bean
  @ConfigurationProperties(prefix = "app.log.mask-converter")
  MaskConverter maskConverter() {
    return new MaskConverter();
  }

  @Configuration
  static class ProxyConfig {

    @Autowired
    void initMaskConverter(MaskConverter maskConverter) {
      MaskConverterLateInitProxy.setConverter(maskConverter);
    }
  }

}
