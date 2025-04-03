package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.core.config_vars.EnvironmentConfigTest.TestProps;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

@ExtendWith(SystemStubsExtension.class)
@SpringBootTest(classes = {ExampleServiceConfig.class, TestProps.class})
@EnableConfigurationProperties
@Slf4j
@ActiveProfiles("test")
class EnvironmentConfigTest {

  @ConfigurationProperties(prefix = "test-props")
  @Data
  static class TestProps {
    private String key1;
    private String key2;
    private String key3;
    private String key4;
    private String complexKeyName;
    private Map<String, String> map;
  }

  @SystemStub
  private static EnvironmentVariables envVars = new EnvironmentVariables()
      .set("SPRING_APPLICATION_JSON",
          """
              { "example-service": { "settings": { "id": "inline-json-example-service" }} }
              """)
      .set("example-service.settings.id", "env-example-service")
      .set("example-service.settings.name", "env-example-service")
      .set("example-service.settings.enabled", "true")
      .set("TEST_PROPS_KEY2", "env-key2")
      .set("test-props.key4", "env-key4")
      .set("TEST_PROPS_COMPLEX_KEY_NAME", "env-complexKeyName");
  @SystemStub
  private static SystemProperties systemProps = new SystemProperties()
      .set("example-service.settings.id", "sysprop-example-service")
      .set("example-service.settings.name", "sysprop-example-service")
      .set("example-service.settings.endpoint", "http://sysprop-example-service.com/v1")
      .set("test-props.key3", "sysprop-key3")
      .set("test-props.key4", "sysprop-key4");
  @Autowired
  private ExampleServiceConfig config;
  @Autowired
  private TestProps testProps;
  @Value("${test-props.complex-key-name}")
  private String testPropsComplexKeyName_correct;
  @Value("${test-props.ComplexKeyName}")
  private String testPropsComplexKeyName_incorrect;
  @Value("${test-props.complexKeyName}")
  private String testPropsComplexKeyName_incorrect2;

  @Test
  void var_source_priority() {
    var prop = "example-service.settings.name";
    var env = System.getenv(prop);
    var sysProp = System.getProperty(prop);
    log.debug("e: {} | sp: {}", env, sysProp);

    log.debug("environment: {}", System.getenv());
    log.debug("systemProperties: {}", System.getProperties());

    log.debug("config: {}", config);

    assertThat(config.getEnabled())
        .isTrue();
    assertThat(config.getId())
        .isEqualTo("inline-json-example-service");
    assertThat(config.getName())
        .isEqualTo("sysprop-example-service");
    assertThat(config.getEndpoint())
        .isEqualTo("http://sysprop-example-service.com/v1");
    assertThat(testProps.getKey1())
        .isEqualTo("yaml-key1");
    assertThat(testProps.getKey2())
        .isEqualTo("env-key2");
    assertThat(testProps.getKey3())
        .isEqualTo("sysprop-key3");
    assertThat(testProps.getKey4())
        .isEqualTo("sysprop-key4");

    // "complex keys"
    assertThat(testProps.getComplexKeyName())
        .isEqualTo("env-complexKeyName");
    assertThat(testPropsComplexKeyName_correct)
        .isEqualTo("env-complexKeyName");
    assertThat(testPropsComplexKeyName_incorrect)
        .isEqualTo("${test-props.ComplexKeyName}");
    assertThat(testPropsComplexKeyName_incorrect2)
        .isEqualTo("${test-props.complexKeyName}");
  }

}
