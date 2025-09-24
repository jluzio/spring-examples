package com.example.spring.batch.playground.user_posts.config;

import static com.example.spring.batch.playground.user_posts.config.UserBatchConfig.DATA_POPULATOR_ENABLED_KEY;

import com.example.spring.batch.playground.config.DataPopulators;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
@ConditionalOnProperty(DATA_POPULATOR_ENABLED_KEY)
public class DataPopulatorConfig {

  @Bean
  public Jackson2RepositoryPopulatorFactoryBean dataDataPopulator() {
    return DataPopulators.dataPopulator("data.json");
  }
}
