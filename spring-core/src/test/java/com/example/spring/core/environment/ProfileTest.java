package com.example.spring.core.environment;

import com.example.spring.core.profile.Development;
import com.example.spring.core.profile.Production;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("development")
@Slf4j
public class ProfileTest {

  @Autowired
  List<String> values;

  @Test
  void test() {
    log.info("values: {}", values);
  }

  @Configuration
  static class Config {

    @Bean
    String bean_1() {
      return "config-1";
    }

    @Bean
    @Profile("development")
    String bean_2_test() {
      return "config-2-dev";
    }

    @Bean
    @Profile("production")
    String bean_3_prd() {
      return "config-3-prd";
    }
  }

  @Configuration
  @Development
  static class TestConfig {

    @Bean
    String bean_test_1() {
      return "dev-config-1";
    }
  }

  @Configuration
  @Production
  static class PrdConfig {

    @Bean
    String bean_prd_1() {
      return "prd-config-1";
    }
  }

}
