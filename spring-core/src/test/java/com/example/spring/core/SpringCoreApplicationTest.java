package com.example.spring.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class SpringCoreApplicationTest {

  @Autowired
  private String appVersion;

  @Test
  void test() {
    log.info("appVersion: {}", appVersion);
  }

}
