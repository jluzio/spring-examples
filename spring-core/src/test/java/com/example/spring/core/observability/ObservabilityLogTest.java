package com.example.spring.core.observability;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureObservability
@Slf4j
class ObservabilityLogTest {

  @Test
  void test() {
    Trace
    log.info("test");
  }

}
