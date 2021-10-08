package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

@ExtendWith(SystemStubsExtension.class)
@SpringBootTest(classes = ExampleServiceConfig.class)
@EnableConfigurationProperties
@Slf4j
class EnvironmentConfigTest {

  @SystemStub
  private static EnvironmentVariables envVars = new EnvironmentVariables()
      .set("SPRING_APPLICATION_JSON", "{ \"example-service\": { \"settings\": { \"id\": \"app-json-example-service\" } } }")
      .set("example-service.settings.enabled", "true")
      .set("example-service.settings.name", "env-example-service")
      .set("example-service.settings.id", "env-example-service");
  @SystemStub
  private static SystemProperties systemProps = new SystemProperties()
      .set("example-service.settings.name", "prop-example-service")
      .set("example-service.settings.id", "prop-example-service")
      .set("example-service.settings.endpoint", "http://prop-example-service.com/v1");
  @Autowired
  private ExampleServiceConfig config;
  @Value("${app.some-service.endpoint}")
  private String someAppValue;

  @Test
  void var_source_priority() {
    var prop = "example-service.settings.name";
    var env = System.getenv(prop);
    var sysProp = System.getProperty(prop);
    log.debug("e: {} | sp: {}", env, sysProp);

    log.debug("config: {}", config);

    assertThat(config.getEnabled())
        .isEqualTo(true);
    assertThat(config.getId())
        .isEqualTo("app-json-example-service");
    assertThat(config.getName())
        .isEqualTo("prop-example-service");
    assertThat(config.getEndpoint())
        .isEqualTo("http://prop-example-service.com/v1");
    assertThat(someAppValue)
        .isEqualTo("http://some-service-endpoint:8080");
  }

}
