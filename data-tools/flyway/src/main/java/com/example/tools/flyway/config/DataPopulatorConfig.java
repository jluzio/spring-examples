package com.example.tools.flyway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.init.JacksonRepositoryPopulatorFactoryBean;

@Configuration
public class DataPopulatorConfig {

  @Bean
  public JacksonRepositoryPopulatorFactoryBean dataDataPopulator() {
    return DataPopulators.dataPopulator("data.json");
  }
}
