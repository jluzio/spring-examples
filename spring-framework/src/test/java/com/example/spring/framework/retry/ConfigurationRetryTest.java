package com.example.spring.framework.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.resilience.annotation.Retryable;

@SpringBootTest(properties = {
    "svc.maxRetries=1",
    "svc.delay=150"
})
@Slf4j
class ConfigurationRetryTest {

  @Configuration
  @EnableResilientMethods
  @Import({FaultyService.class})
  static class Config {

  }

  public static class FaultyService {

    @Getter
    AtomicInteger callCounter = new AtomicInteger(0);

    @Retryable(maxRetriesString = "#{${svc.maxRetries}}", delayString = "#{${svc.delay}}")
    public void faultyCall(int faults) {
      if (callCounter.incrementAndGet() > faults) {
        log.debug("faultyCall :: success");
      } else {
        log.debug("faultyCall :: error");
        throw new IllegalArgumentException("Some exception");
      }
    }

    public void clear() {
      callCounter.set(0);
    }
  }


  @Autowired
  FaultyService faultyService;

  @BeforeEach
  void clear() {
    faultyService.clear();
  }

  @Test
  void test_valid_retries() {
    logStart();
    var start = Instant.now();
    assertThatThrownBy(() -> faultyService.faultyCall(2))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(faultyService.getCallCounter().get())
        .isEqualTo(2);
    var end = Instant.now();
    logEnd();
    var duration = Duration.between(start, end);
    log.debug("time spent: {}", duration);
    assertThat(duration)
        .isGreaterThanOrEqualTo(Duration.ofMillis(150));
  }

  private void logStart() {
    log.debug("=== start ===");
  }

  private void logEnd() {
    log.debug("=== end ===");
  }

}
