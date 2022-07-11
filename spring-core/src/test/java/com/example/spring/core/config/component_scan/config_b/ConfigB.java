package com.example.spring.core.config.component_scan.config_b;

import com.example.spring.core.config.component_scan.DataBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigB {

  @Bean("configB-dataBean")
  @DataBean
  String dataBean() {
    return "configB-dataBean";
  }

}
