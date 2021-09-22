package com.example.spring.batch.playground.guide.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
public class DataConfig {

  @Bean
  public Jackson2RepositoryPopulatorFactoryBean getDataPopulator() {
    Jackson2RepositoryPopulatorFactoryBean factoryBean = new Jackson2RepositoryPopulatorFactoryBean();
    factoryBean.setResources(new Resource[]{new ClassPathResource("data.json")});
    return factoryBean;
  }

}
