package com.example.spring.framework.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test-group")
class ProfileGroupTest {

  @Autowired
  Environment environment;

  @Configuration
  static class Config {

  }

  @Test
  void test() {
    assertThat(environment.getActiveProfiles())
        .containsExactlyInAnyOrder("test-group", "test1", "test2");
    assertThat(environment.getProperty("test-group.value"))
        .isNotEmpty();
    assertThat(environment.getProperty("test1.value"))
        .isNotEmpty();
    assertThat(environment.getProperty("test2.value"))
        .isNotEmpty();
  }

}
