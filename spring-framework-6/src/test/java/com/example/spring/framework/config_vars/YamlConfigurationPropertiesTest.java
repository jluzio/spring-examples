package com.example.spring.framework.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.framework.config_vars.YamlConfigurationPropertiesTest.MapConfig;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {ExampleServiceConfig.class, MapConfig.class},
    properties = {
        "map-cfg.map.simple_key: value1",
        "map-cfg.map.[/escaped_key]: value2",
        "map-cfg.map./non_escaped_key: value3",
    }
)
@EnableConfigurationProperties
@ActiveProfiles("yaml")
@Slf4j
class YamlConfigurationPropertiesTest {

  @Autowired
  ExampleServiceConfig config;
  @Autowired
  MapConfig mapConfig;


  @Configuration(proxyBeanMethods = false)
  @ConfigurationProperties("map-cfg")
  @Data
  static class MapConfig {

    private Map<String, String> map;
  }

  @Test
  void test() {
    log.info("{}", config);

    assertThat(config)
        .isEqualTo(ExpectedTestValues.exampleServiceConfig());
  }

  @Test
  void test_map() {
    assertThat(mapConfig.getMap())
        .isEqualTo(Map.of(
            "simple_key", "value1",
            "/escaped_key", "value2",
            "non_escaped_key", "value3"
        ));
  }
}
