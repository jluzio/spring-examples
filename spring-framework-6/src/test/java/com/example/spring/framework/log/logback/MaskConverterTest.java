package com.example.spring.framework.log.logback;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = MaskConverterTest.Config.class)
@EnableConfigurationProperties
@Slf4j
class MaskConverterTest {

  @Configuration
  @Import(LoggingConfig.class)
  static class Config {

  }

  @Autowired
  MaskConverter converter;

  @Test
  void test() {
    assertThat(converter.transform(null, "1234561234561234"))
        .isEqualTo("123456XXXXXX1234");
    assertThat(converter.transform(null, "Number: 1234561234561234"))
        .isEqualTo("Number: 123456XXXXXX1234");
    assertThat(converter.transform(null, "Number: 123456123456781234"))
        .isEqualTo("Number: 123456XXXXXX1234");
    assertThat(converter.transform(null, "Number: 1234561234567890121234"))
        .isEqualTo("Number: 1234561234567890121234");
  }

}