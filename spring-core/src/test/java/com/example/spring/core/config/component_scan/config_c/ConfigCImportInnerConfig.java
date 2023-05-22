package com.example.spring.core.config.component_scan.config_c;

import com.example.spring.core.config.component_scan.DataBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ConfigCImportInnerConfig.Config.class)
class ConfigCImportInnerConfig {

  static class Config {

    @Bean("configC-importInnerConfig-dataBean")
    @DataBean
    String dataBean() {
      return "configC-importInnerConfig-dataBean";
    }
  }

}
