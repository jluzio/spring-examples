package com.example.spring.batch.playground.user_posts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

public class DataPopulatorConfig {

  @Bean
  public Jackson2RepositoryPopulatorFactoryBean getDataPopulator() {
    Jackson2RepositoryPopulatorFactoryBean factoryBean = new Jackson2RepositoryPopulatorFactoryBean();
    factoryBean.setResources(new Resource[]{new ClassPathResource("data.json")});
    return factoryBean;
  }

}
