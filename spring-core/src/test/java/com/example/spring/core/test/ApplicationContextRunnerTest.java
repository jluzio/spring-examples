package com.example.spring.core.test;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.core.test.ApplicationContextRunnerTest.TestConfig.SomeBean;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;

class ApplicationContextRunnerTest {

  @TestConfiguration
  @EnableConfigurationProperties
  protected static class TestConfig {

    static class SomeBean {

    }

    @Bean
    String stringBean() {
      return "test-string-bean";
    }

    @Bean
    SomeBean someBean() {
      return new SomeBean();
    }

  }

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      // ConfigDataApplicationContextInitializer: trigger loading of ConfigData such as application.properties
      .withInitializer(new ConfigDataApplicationContextInitializer())
      .withUserConfiguration(TestConfig.class);
  // If needed to load web contexts (including mocks for web)
  private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
      // ConfigDataApplicationContextInitializer: trigger loading of ConfigData such as application.properties
      .withInitializer(new ConfigDataApplicationContextInitializer())
      .withUserConfiguration(TestConfig.class);

  @Test
  void testContextLoad() {
    contextRunner.run(context -> {
      assertThat(context).hasNotFailed();
      assertThat(context).hasSingleBean(SomeBean.class);
      assertThat(context).hasBean("stringBean");
    });
  }

}
