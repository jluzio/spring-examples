package com.example.spring.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// @ComponentScan(basePackages="com.example.spring")
@PropertySource("classpath:application.properties")
public class AppConfig {

  @Bean
  @Qualifier("appVersion")
  public String appVersion() {
    return "App@1.0";
  }

}
