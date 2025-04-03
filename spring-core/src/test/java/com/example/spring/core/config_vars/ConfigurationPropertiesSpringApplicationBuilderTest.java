package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

@Log4j2
class ConfigurationPropertiesSpringApplicationBuilderTest {

  @Configuration
  @EnableConfigurationProperties(ConfigProps.class)
  static class Config {

  }

  @ConfigurationProperties("configprops")
  @Data
  static class ConfigProps {

    private String prop1;
    private String prop2 = "v2";
    private String prop3 = "v3";

  }

  @Test
  void test_default() {
    appCtxRunner()
        .withPropertyValues("spring.profiles.active=test")
        .run(ctx -> {
              var configProps = ctx.getBean(ConfigProps.class);
              log.debug(configProps);

              assertThat(configProps)
                  .hasNoNullFieldsOrProperties()
                  .satisfies(it -> assertThat(it.getProp1()).isEqualTo("appval1"));
            }
        );
  }

  @Test
  void test_custom_props() {
    appCtxRunner()
        .withPropertyValues(
            "configprops.prop1=value1",
            "configprops.prop2=value2",
            "configprops.prop3=value3"
        )
        .run(ctx -> {
          var configProps = ctx.getBean(ConfigProps.class);
          log.debug(configProps);

          assertThat(configProps)
              .hasNoNullFieldsOrProperties()
              .satisfies(it -> assertThat(it.getProp1()).isEqualTo("value1"));
        });
  }

  @Test
  void test_optionals() {
    appCtxRunner()
        .withPropertyValues(
            "configprops.prop1=value1"
        )
        .run(ctx -> {
          var configProps = ctx.getBean(ConfigProps.class);
          log.debug(configProps);

          assertThat(configProps)
              .hasNoNullFieldsOrProperties()
              .satisfies(it -> assertThat(it.getProp1()).isEqualTo("value1"))
              .satisfies(it -> assertThat(it.getProp2()).isEqualTo("v2"))
          ;
        });
  }

  private ApplicationContextRunner appCtxRunner() {
    return new ApplicationContextRunner()
        .withUserConfiguration(Config.class)
        .withInitializer(new ConfigDataApplicationContextInitializer());
  }
}
