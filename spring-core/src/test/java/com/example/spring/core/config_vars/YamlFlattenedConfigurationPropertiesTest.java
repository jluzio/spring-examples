package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(ExampleServiceConfig.class)
@EnableConfigurationProperties
@ActiveProfiles("yaml-flattened")
@Slf4j
class YamlFlattenedConfigurationPropertiesTest {

  @Autowired
  ExampleServiceConfig config;

  @Test
  void test() {
    log.info("{}", config);

    assertThat(config)
        .isEqualTo(ExpectedTestValues.exampleServiceConfig());
  }
}
