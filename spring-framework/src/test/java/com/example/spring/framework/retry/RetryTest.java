package com.example.spring.framework.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.springframework.stereotype.Service;

@SpringBootTest
@Slf4j
class RetryTest {

  @Configuration
  @EnableResilientMethods
  @Import({FaultyService.class})
  static class Config {

  }

  public static class BaseFaultyService {

    @Getter
    AtomicInteger callCounter = new AtomicInteger(0);

    @Retryable(maxRetries = 2, delay = 100L)
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

  @Service
  public static class FaultyService extends BaseFaultyService {

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
    assertThatCode(() -> faultyService.faultyCall(2))
        .doesNotThrowAnyException();
    assertThat(faultyService.getCallCounter().get())
        .isEqualTo(3);
    logEnd();
  }

  @Test
  void test_invalid_retries() {
    logStart();
    assertThatThrownBy(() -> faultyService.faultyCall(3))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(faultyService.getCallCounter().get())
        .isEqualTo(3);
    logEnd();
  }

  private void logStart() {
    log.debug("=== start ===");
  }

  private void logEnd() {
    log.debug("=== end ===");
  }

}
