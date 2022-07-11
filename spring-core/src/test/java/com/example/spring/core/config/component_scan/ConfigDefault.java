package com.example.spring.core.config.component_scan;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigDefault {

  @Bean("configDefault-dataBean")
  @DataBean
  String dataBean() {
    return "configDefault-dataBean";
  }

}
