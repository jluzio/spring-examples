package com.example.spring.core.log.logback;

import java.util.regex.Pattern;
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
  @Import(MaskConverterConfig.class)
  static class Config {

  }

  @Autowired
  MaskConverter converter;

  @Test
  void test() {
    log.info("{}", converter);
    Pattern pattern = Pattern.compile("\\b([0-9]{6})([0-9]{6,10})([0-9]{4})\\b");
    log.info("{}", pattern);
    log.info(converter.transform(null, "1234 1234561234561234"));
  }

}