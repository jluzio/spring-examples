package com.example.spring.core.log;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class LogTest {

  @Test
  void test() {
    log.info("Hello {}!", "world");
  }

}
