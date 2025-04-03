package com.example.spring.core.config.auto_configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class AutoConfigurationImportTest {

  @Autowired
  ApplicationContext context;

  @Test
  void test() {
    assertThat(context.getBean("autoConfiguredBean"))
        .isEqualTo("autoConfiguredBean");
    assertThat(context.getBean("innerConfigBean"))
        .isEqualTo("innerConfigBean");
    assertThat(context.getBean("someImportedConfigBean"))
        .isEqualTo("someImportedConfigBean");

    // imported by ComponentScan, will exist
    assertThat(context.containsBean("someNotImportedConfigBean"))
        .isTrue();
    // not imported by AutoConfiguration or ComponentScan, will not exist
    assertThat(context.containsBean("someNotImportedAutoConfigBean"))
        .isFalse();
  }
}
