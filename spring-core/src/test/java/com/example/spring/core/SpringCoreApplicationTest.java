package com.example.spring.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class SpringCoreApplicationTest {
  @Autowired
  private String appVersion;

  @Test
  void test() {
    log.info("appVersion: {}", appVersion);
  }

}
