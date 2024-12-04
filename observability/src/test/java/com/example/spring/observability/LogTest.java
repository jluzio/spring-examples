package com.example.spring.observability;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Slf4j
class LogTest {

  @TestConfiguration
  // needed?
//  @AutoConfigureObservability
  @Import({SomeSyncService.class})
  static class Config {

  }

  static class SomeSyncService {

    public void execute() {
      log.info("SomeAsyncService.execute()");
    }
  }

  @Autowired
  Tracer tracer;
  @Autowired
  SomeSyncService someSyncService;

  @Test
  void test() {
    tracer.startScopedSpan("test");
    log.info("test");
    someSyncService.execute();
  }
}