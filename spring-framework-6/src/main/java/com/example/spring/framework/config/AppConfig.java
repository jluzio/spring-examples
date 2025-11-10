package com.example.spring.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// @ComponentScan(basePackages="com.example.spring")
@PropertySource("classpath:application.yml")
public class AppConfig {

  @Bean
  public String appVersion() {
    return "App@1.0";
  }

}
