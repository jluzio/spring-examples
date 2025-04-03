package com.example.spring.core.config.auto_configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.core.config.auto_configuration.sample.SampleAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest
class AutoConfigurationImportTest {

  @Autowired
  ApplicationContext context;

  @Test
  void test() {
    assertThat(context.getBean("autoConfiguredBean"))
        .isEqualTo("autoConfiguredBean");
    assertThat(context.getBean("innerBean"))
        .isEqualTo("innerBean");
    assertThat(context.getBean("someImportedBean"))
        .isEqualTo("someImportedBean");
    // this config is included due to ComponentScan instead of AutoConfiguration
    assertThat(context.containsBean("someNotImportedBean"))
        .isTrue();
    assertThat(context.getBean("someNotImportedBean"))
        .isEqualTo("someNotImportedBean");
  }
}
