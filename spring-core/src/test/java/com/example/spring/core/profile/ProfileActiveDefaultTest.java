package com.example.spring.core.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@SpringBootTest
class ProfileActiveDefaultTest {

  @Autowired
  Environment environment;

  @Configuration
  static class Config {

  }

  @Test
  void test() {
    assertThat(environment.getActiveProfiles())
        .containsExactlyInAnyOrder("defaultIncludedProfile1");
  }

}
