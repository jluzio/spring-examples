package com.example.spring.core.environment.conditional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.org.webcompere.systemstubs.SystemStubs.withEnvironmentVariables;

import com.example.spring.core.environment.conditional.ConditionalOnPropertyTest.Config.ConfigProps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

@Slf4j
class ConditionalOnPropertyTest {

  @Configuration
  @EnableConfigurationProperties(ConfigProps.class)
  static class Config {

    @Bean
    @ConditionalOnProperty(value = "config.stringBean1", havingValue = "true")
      // Same as
      // @ConditionalOnProperty(prefix = "config", value = "stringBean1", havingValue = "true")
    String stringBean1() {
      return "stringBean1";
    }

    @Bean
    @ConditionalOnProperty(value = "config.string-bean2", havingValue = "true")
    String stringBean2() {
      return "stringBean2";
    }

    @ConfigurationProperties(prefix = "config")
    @Data
    static class ConfigProps {

      private String stringBean1;
    }

  }

  @Test
  void test_not_defined() {
    runner().run(context -> {
      assertThatThrownBy(() -> context.getBean("stringBean1"))
          .isInstanceOf(NoSuchBeanDefinitionException.class);
    });
  }

  @Test
  void test_defined_property() {
    runner()
        .withPropertyValues("config.stringBean1=true")
        .run(context -> {
          assertThatNoException()
              .isThrownBy(() -> context.getBean("stringBean1"));
        });
  }

  @Test
  void test_defined_env() throws Exception {
    EnvironmentVariables envVars = new EnvironmentVariables()
        .set("config.stringBean1", "true");
    envVars.execute(() -> {
      runner()
          .run(context -> {
            assertThatNoException()
                .isThrownBy(() -> context.getBean("stringBean1"));
          });
    });
  }

  @Test
  void test_defined_env_screaming_case() throws Exception {
    withEnvironmentVariables()
        .set("CONFIG_STRINGBEAN1", "true")
        .set("CONFIG_STRINGBEAN2", "true")
        .execute(() -> {
          runner()
              .run(context -> {
                assertThatNoException()
                    .isThrownBy(() -> context.getBean("stringBean1"));
                assertThatNoException()
                    .isThrownBy(() -> context.getBean("stringBean2"));
                var configProps = context.getBean(ConfigProps.class);
                assertThat(configProps.getStringBean1())
                    .isEqualTo("true");
              });
        });
  }


  @Test
  void test_defined_env_screaming_case_usual_naming_for_props() throws Exception {
    withEnvironmentVariables()
        .set("CONFIG_STRING_BEAN1", "true")
        .set("CONFIG_STRING_BEAN2", "true")
        .execute(() -> {
          runner()
              .run(context -> {
                // camelCase vars aren't handled the same way ConfigurationProperties are
                assertThatThrownBy(() -> context.getBean("stringBean1"))
                    .isInstanceOf(NoSuchBeanDefinitionException.class);
                assertThatNoException()
                    .isThrownBy(() -> context.getBean("stringBean2"));
                var configProps = context.getBean(ConfigProps.class);
                assertThat(configProps.getStringBean1())
                    .isEqualTo("true");
              });
        });
  }

  private ApplicationContextRunner runner() {
    return new ApplicationContextRunner()
        .withUserConfiguration(Config.class);
  }

}
