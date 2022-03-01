package com.example.spring.core.beans;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = ValueTest.Config.class)
@Slf4j
class ValueTest {

  @Configuration
  static class Config {

  }

  @Value("${logging.level.com.example.spring:value-if-not-exists}")
  String someProp;
  @Value("${test.hello:value-if-not-exists}")
  String stringProp;
  @Value("${test.csv}")
  List<Integer> csvProp;
  @Value("${test.csv}")
  List<String> strCsvProp;
  @Value("#{'${test.hello} world!'}")
  String stringPropEl;

  @Test
  void test() {
    log.info("someProp: {}", someProp);
    log.info("stringProp: {}", stringProp);
    log.info("csvProp: {}", csvProp);
    log.info("strCsvProp: {}", strCsvProp);
    log.info("stringPropEl: {}", stringPropEl);
  }
}
