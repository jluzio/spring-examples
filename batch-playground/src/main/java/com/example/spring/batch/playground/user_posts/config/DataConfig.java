package com.example.spring.batch.playground.user_posts.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
@ConditionalOnProperty("app.batch.auto.enabled")
public class DataConfig {

  @Bean
  public Jackson2RepositoryPopulatorFactoryBean getDataPopulator() {
    Jackson2RepositoryPopulatorFactoryBean factoryBean = new Jackson2RepositoryPopulatorFactoryBean();
    factoryBean.setResources(new Resource[]{new ClassPathResource("data.json")});
    return factoryBean;
  }

}
