package com.example.spring.cloud.configclient.config;

import com.example.spring.cloud.configclient.config.model.SimpleValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleValuesConfig {

  @Bean
  public SimpleValue simpleValue(@Value("${app.features.feat1.prop1}") String value) {
    return new SimpleValue(value);
  }

  @Bean
  @RefreshScope
  public SimpleValue refreshableSimpleValue(@Value("${app.features.feat1.prop1}") String value) {
    return new SimpleValue(value);
  }
}
