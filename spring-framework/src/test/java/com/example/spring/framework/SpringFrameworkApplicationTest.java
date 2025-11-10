package com.example.spring.framework;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class SpringFrameworkApplicationTest {

  @Autowired
  private String appVersion;

  @Test
  void contextLoads() {
    log.info("appVersion: {}", appVersion);
  }

}
