package com.example.spring.framework.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("import")
class ImportTest {

  @Configuration
  static class Config {

  }


  @Autowired
  Environment environment;

  @Test
  void test() {
    assertThat(environment.getActiveProfiles())
        .containsExactlyInAnyOrder("import");
    assertThat(environment.getRequiredProperty("app.some-bean"))
        .isEqualTo("SomeBean");
  }

}
