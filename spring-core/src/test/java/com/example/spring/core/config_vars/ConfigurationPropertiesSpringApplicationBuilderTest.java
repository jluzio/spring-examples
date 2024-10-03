package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
    try (
        var app = new SpringApplicationBuilder()
            .sources(Config.class)
            .web(WebApplicationType.NONE)
            .profiles("test")
            .run()
    ) {
      var configProps = app.getBean(ConfigProps.class);
      log.debug(configProps);

      assertThat(configProps)
          .hasNoNullFieldsOrProperties()
          .satisfies(it -> assertThat(it.getProp1()).isEqualTo("appval1"));
    }
  }

  @Test
  void test_custom_props() {
    try (
        var app = new SpringApplicationBuilder()
            .sources(Config.class)
            .web(WebApplicationType.NONE)
            .properties(
                "configprops.prop1=value1",
                "configprops.prop2=value2",
                "configprops.prop3=value3"
            )
            .run()
    ) {
      var configProps = app.getBean(ConfigProps.class);
      log.debug(configProps);

      assertThat(configProps)
          .hasNoNullFieldsOrProperties()
          .satisfies(it -> assertThat(it.getProp1()).isEqualTo("value1"));
    }
  }

  @Test
  void test_optionals() {
    try (
        var app = new SpringApplicationBuilder()
            .sources(Config.class)
            .web(WebApplicationType.NONE)
            .properties(
                "configprops.prop1=value1"
            )
            .run()
    ) {
      var configProps = app.getBean(ConfigProps.class);
      log.debug(configProps);

      assertThat(configProps)
          .hasNoNullFieldsOrProperties()
          .satisfies(it -> assertThat(it.getProp1()).isEqualTo("value1"))
          .satisfies(it -> assertThat(it.getProp2()).isEqualTo("v2"))
      ;
    }
  }

}
