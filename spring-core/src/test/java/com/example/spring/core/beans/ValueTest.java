package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    properties = {
        "test.hello=hello fren",
        "test.csv=1,2,3"
    }
)
@Slf4j
@ActiveProfiles("test")
@EnableConfigurationProperties
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
    assertThat(someProp).isEqualTo("DEBUG");
    assertThat(stringProp).isEqualTo("hello fren");
    assertThat(csvProp).isEqualTo(List.of(1, 2, 3));
    assertThat(strCsvProp).isEqualTo(List.of("1", "2", "3"));
    assertThat(stringPropEl).isEqualTo("hello fren world!");
  }
}
