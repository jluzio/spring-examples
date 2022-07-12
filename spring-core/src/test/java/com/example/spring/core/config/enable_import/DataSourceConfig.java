package com.example.spring.core.config.enable_import;

import org.springframework.context.annotation.Bean;

public class DataSourceConfig {

  @Bean
  String meaningOfLifeDataSource() {
    return "42";
  }

}
