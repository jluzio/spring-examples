package com.example.spring.data.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
public class DataPopulatorConfig {

  @Bean
  public Jackson2RepositoryPopulatorFactoryBean dataDataPopulator() {
    return dataPopulator("data.json");
  }

  private Jackson2RepositoryPopulatorFactoryBean dataPopulator(String... resources) {
    Jackson2RepositoryPopulatorFactoryBean factoryBean = new Jackson2RepositoryPopulatorFactoryBean();
    factoryBean.setResources(
        Arrays.stream(resources)
            .map(ClassPathResource::new)
            .toArray(Resource[]::new));
    return factoryBean;
  }
}
