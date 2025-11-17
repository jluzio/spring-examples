package com.example.spring.framework.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(properties = {
    """
      logging.group.myGroup: com.example.custom1,com.example.custom2
      logging.level.myGroup: trace
    """
})
@Slf4j
class LogTest {

  @Configuration
  static class Config {

  }

  @Test
  void test() {
    log.info("Hello {}!", "world");
    log.trace("default trace");
  }

  @Test
  void test_groups() {
    var logCustom1 = LoggerFactory.getLogger("com.example.custom1");
    logCustom1.trace("testing group - custom1");

    var logCustom2 = LoggerFactory.getLogger("com.example.custom2");
    logCustom2.trace("testing group - custom2");

    // NOTE: Spring Boot uses groups (web, sql)
  }

}
