package com.example.spring.core.config.merge_profiles;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "debug=true")
class JavaConfigMergeProfilesTest {

  @Configuration
//  @EnableAutoConfiguration
  @Import({Config1.class})
  static class Config {

  }

  @EnableAutoConfiguration()
  static class Config1 {

  }

  @Test
  void test() {
    IO.println("test");
  }

}
