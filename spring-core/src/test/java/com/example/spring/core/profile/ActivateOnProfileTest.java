package com.example.spring.core.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"test", "test-addon"})
class ActivateOnProfileTest {

  @Configuration
  @EnableConfigurationProperties
  static class Config {

    @Bean
    @ConfigurationProperties(prefix = "test-props")
    Map<String, String> testProps() {
      return new HashMap<>();
    }

  }


  @Autowired
  Environment environment;
  @Autowired
  Map<String, String> testProps;

  @Test
  void test() {
    assertThat(environment.getActiveProfiles())
        .containsExactlyInAnyOrder("test", "test-addon");
    assertThat(testProps)
        .isNotEmpty()
        .hasSize(5)
        .containsKey("key1")
        .containsKey("keyAddon1");
  }

}
