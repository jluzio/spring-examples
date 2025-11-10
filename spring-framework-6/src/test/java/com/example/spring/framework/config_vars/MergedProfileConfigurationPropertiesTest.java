package com.example.spring.framework.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
class MergedProfileConfigurationPropertiesTest {

  @Configuration
  @EnableConfigurationProperties(ConfigProperties.class)
  static class Config {

  }

  @ConfigurationProperties(prefix = "app.merged-profile-bean")
  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  static class ConfigProperties {

    private String name;
    private String value;

  }

  @Autowired
  ConfigProperties properties;

  @Test
  void test() {
    assertThat(properties)
        .isEqualTo(new ConfigProperties("property1", "value2"));
  }

}
