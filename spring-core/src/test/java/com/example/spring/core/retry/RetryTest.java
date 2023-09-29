package com.example.spring.core.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@SpringBootTest
@Slf4j
class RetryTest {

  @Configuration
  @EnableRetry
  @Import({FaultyService.class, FaultyAndRecoverService.class})
  static class Config {

  }

  @Service
  public static class FaultyService {

    @Getter
    AtomicInteger callCounter = new AtomicInteger(0);

    @Retryable(maxAttempts = 3)
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
  public static class FaultyAndRecoverService {

    @Getter
    AtomicInteger callCounter = new AtomicInteger(0);

    @Retryable(maxAttempts = 3)
    public void faultyCall(int faults) {
      if (callCounter.incrementAndGet() > faults) {
        log.debug("faultyCall :: success");
      } else {
        log.debug("faultyCall :: error");
        throw new IllegalArgumentException("Some exception");
      }
    }

    @Recover
    public void faultyCallRecover(IllegalArgumentException e, int faults) {
      log.debug("faultyCallRecover :: {} | {}", e.getMessage(), faults);
      throw new RetryException("max retries reached", e);
    }

    public void clear() {
      callCounter.set(0);
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
    assertThatCode(() -> faultyService.faultyCall(2))
        .doesNotThrowAnyException();
    assertThat(faultyService.getCallCounter().get())
        .isEqualTo(3);
  }

  @Test
  void test_invalid_retries() {
    assertThatThrownBy(() -> faultyService.faultyCall(3))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(faultyService.getCallCounter().get())
        .isEqualTo(3);
  }

  @Test
  void test_custom_recover() {
    assertThatThrownBy(() -> faultyAndRecoverService.faultyCall(3))
        .isInstanceOf(RetryException.class);
    assertThat(faultyAndRecoverService.getCallCounter().get())
        .isEqualTo(3);
  }

}
