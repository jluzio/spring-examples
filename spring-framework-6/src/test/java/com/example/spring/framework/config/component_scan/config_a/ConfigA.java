package com.example.spring.framework.config.component_scan.config_a;

import com.example.spring.framework.config.component_scan.DataBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigA {

  @Bean("configA-dataBean")
  @DataBean
  String dataBean() {
    return "configA-dataBean";
  }

}
