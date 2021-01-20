package com.example.spring.core.config_props;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(ServiceProperties.class)
@EnableConfigurationProperties
@ActiveProfiles("test")
@Slf4j
public class YamlConfigurationPropertiesTest {

  @Autowired
  ServiceProperties properties;

  @Test
  void test() {
    log.info("{}", properties);
  }
}
