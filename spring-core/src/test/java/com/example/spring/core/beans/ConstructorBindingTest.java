package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(properties = {
    "config.key=key1",
    "config.value=val1",
})
@Slf4j
class ConstructorBindingTest {

  @ConfigurationProperties("config")
  @Value
  static class ImmutablePojoConfig {

    String key;
    String value;
  }

  @ConfigurationProperties("config")
  record RecordConfig(String key, String value) {

  }

  @ConfigurationProperties("config")
  @Value
  static class ImmutablePojoMultipleConstructorsConfig {

    String key;
    String value;

    @ConstructorBinding
    public ImmutablePojoMultipleConstructorsConfig(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public ImmutablePojoMultipleConstructorsConfig(String key) {
      this(key, null);
    }
  }

  @Configuration
  @EnableConfigurationProperties({ImmutablePojoConfig.class, RecordConfig.class,
      ImmutablePojoMultipleConstructorsConfig.class})
  static class Config {

  }

  @Autowired
  ImmutablePojoConfig immutablePojoConfig;
  @Autowired
  RecordConfig recordConfig;
  @Autowired
  ImmutablePojoMultipleConstructorsConfig immutablePojoMultipleConstructorsConfig;

  @Test
  void test() {
    log.debug("immutablePojoConfig: {}", immutablePojoConfig);
    assertThat(immutablePojoConfig)
        .hasNoNullFieldsOrProperties();
    log.debug("recordConfig: {}", recordConfig);
    assertThat(recordConfig)
        .hasNoNullFieldsOrProperties();
    log.debug("immutablePojoMultipleConstructorsConfig: {}", immutablePojoMultipleConstructorsConfig);
    assertThat(immutablePojoMultipleConstructorsConfig)
        .hasNoNullFieldsOrProperties();
  }

}
