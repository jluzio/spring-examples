package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.mock.env.MockEnvironment;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

@ExtendWith(SystemStubsExtension.class)
@Slf4j
class SpringApplicationBuilderTest {

  @TestConfiguration
  @EnableConfigurationProperties
  static class Config {

  }

  @Test
  void var_source_priority() {
    var app = new SpringApplicationBuilder(Config.class, ExampleServiceConfig.class)
        .web(WebApplicationType.NONE)
        .environment(new MockEnvironment()
            .withProperty("example-service.settings.enabled", "true")
            .withProperty("example-service.settings.name", "mock-example-service")
            .withProperty("example-service.settings.endpoint", "http://mock-example-service.com/v1")
        );
    try (var context = app.run()) {
      var prop = "example-service.settings.name";
      var env = System.getenv(prop);
      var sysProp = System.getProperty(prop);
      log.debug("e: {} | sp: {}", env, sysProp);

      ExampleServiceConfig config = context.getBean(ExampleServiceConfig.class);
      log.debug("config: {}", config);

      assertThat(config.getEnabled())
          .isEqualTo(true);
      assertThat(config.getName())
          .isEqualTo("mock-example-service");
      assertThat(config.getEndpoint())
          .isEqualTo("http://mock-example-service.com/v1");
    }
  }

}
