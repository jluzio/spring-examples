package com.example.spring.core.retry;

import static java.util.Objects.requireNonNull;
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
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

@SpringBootTest
@Slf4j
class RetryTest {

  @Configuration
  @EnableRetry
  @Import({FaultyService.class, FaultyAndRecoverService.class})
  static class Config {

  }

  public static class BaseFaultyService {

    @Getter
    AtomicInteger callCounter = new AtomicInteger(0);

    @Retryable(maxAttempts = 3, backoff = @Backoff(100L))
    public void faultyCall(int faults) {
      var retryCount = requireNonNull(RetrySynchronizationManager.getContext()).getRetryCount();
      if (callCounter.incrementAndGet() > faults) {
        log.debug("faultyCall({}) :: success", retryCount);
      } else {
        log.debug("faultyCall({}) :: error", retryCount);
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

  @Service
  public static class FaultyAndRecoverService extends BaseFaultyService {

    @Recover
    public void faultyCallRecover(IllegalArgumentException e, int faults) {
      log.debug("faultyCallRecover :: {} | {}", e.getMessage(), faults);
      throw new RetryException("max retries reached", e);
    }
  }

  @Autowired
  FaultyService faultyService;
  @Autowired
  FaultyAndRecoverService faultyAndRecoverService;

  @BeforeEach
  void clear() {
    faultyService.clear();
    faultyAndRecoverService.clear();
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

  @Test
  void test_custom_recover() {
    logStart();
    assertThatThrownBy(() -> faultyAndRecoverService.faultyCall(3))
        .isInstanceOf(RetryException.class);
    assertThat(faultyAndRecoverService.getCallCounter().get())
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
