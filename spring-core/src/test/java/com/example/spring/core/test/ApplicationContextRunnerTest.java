package com.example.spring.core.test;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.core.test.ApplicationContextRunnerTest.TestConfig.SomeBean;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
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

//  private final WebApplicationContextRunner appCtxRunner = new WebApplicationContextRunner()
//  private final ReactiveWebApplicationContextRunner appCtxRunner = new ReactiveWebApplicationContextRunner()

  /**
   * Use AbstractApplicationContextRunner instance that suits the test.
   * <ul>
   * <li>ApplicationContextRunner for core</li>
   * <li>WebApplicationContextRunner for web</li>
   * <li>ReactiveWebApplicationContextRunner for reactive web</li>
   * </ul>
   * @see org.springframework.boot.test.context.runner.AbstractApplicationContextRunner
   * @see org.springframework.boot.test.context.runner.ApplicationContextRunner
   * @see org.springframework.boot.test.context.runner.WebApplicationContextRunner
   * @see org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner
   */
  private final ApplicationContextRunner appCtxRunner = new ApplicationContextRunner()
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
