package com.example.spring.framework.config.enable_import;

import org.springframework.context.annotation.Bean;

public class DataSourceConfig {

  @Bean
  String meaningOfLifeDataSource() {
    return "42";
  }

}
