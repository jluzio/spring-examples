package com.example.tools.flyway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
public class DataPopulatorConfig {

  @Bean
  public Jackson2RepositoryPopulatorFactoryBean dataDataPopulator() {
    return DataPopulators.dataPopulator("data.json");
  }
}
