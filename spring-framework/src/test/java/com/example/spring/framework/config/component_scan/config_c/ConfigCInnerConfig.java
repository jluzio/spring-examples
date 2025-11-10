package com.example.spring.framework.config.component_scan.config_c;

import com.example.spring.framework.config.component_scan.DataBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ConfigCInnerConfig {

  @Configuration
  static class Config {

    @Bean("configC-innerConfig-dataBean")
    @DataBean
    String dataBean() {
      return "configC-innerConfig-dataBean";
    }
  }

}
