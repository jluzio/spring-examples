package com.example.spring.framework.retry;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

@SpringBootTest
@Slf4j
class RetryListenerAndStatefulTest {

  @Configuration
  @EnableRetry
  @Import({FaultyService.class, LogRetryListener.class})
  static class Config {

    @Bean
    LogRetryListener logRetryListener() {
      return new LogRetryListener();
    }

  }

  @Slf4j
  public static class LogRetryListener implements RetryListener {

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
      log("open",
          "context", context,
          "callback", callback
      );
      return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
        Throwable throwable) {
      log("close",
          "context", context,
          "callback", callback,
          "throwable", throwable
      );
    }

    @Override
    public <T, E extends Throwable> void onSuccess(RetryContext context, RetryCallback<T, E> callback, T result) {
      log("onSuccess",
          "context", context,
          "callback", callback,
          "result", result
      );
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
        Throwable throwable) {
      log("onError",
          "context", context,
          "callback", callback,
          "throwable", throwable
      );
    }

    private void log(String tag, Object... pairs) {
      var map = new LinkedHashMap<>();
      for (var i = 0; i < pairs.length - 1; i += 2) {
        map.put(pairs[i], pairs[i + 1]);
      }
      log.debug("{} :: {}", tag, map);
    }
  }

  public static class BaseFaultyService {

    @Getter
    AtomicInteger callCounter = new AtomicInteger(0);
    @Getter
    AtomicInteger statefulFaults = new AtomicInteger(0);

    public void baseFaultyCall(int faults) {
      var retryCount = requireNonNull(RetrySynchronizationManager.getContext()).getRetryCount();
      if (callCounter.incrementAndGet() > faults) {
        log.debug("faultyCall({}) :: success", retryCount);
      } else {
        log.debug("faultyCall({}) :: error", retryCount);
        throw new IllegalArgumentException("Some exception");
      }
    }

    public void baseFaultyCallStateful() {
      baseFaultyCall(statefulFaults.get());
    }

    public void clear() {
      callCounter.set(0);
    }
  }

  @Service
  public static class FaultyService extends BaseFaultyService {

    @Retryable(maxAttempts = 3, backoff = @Backoff(100L), listeners = "logRetryListener", stateful = false)
    public void faultyCall(int faults) {
      baseFaultyCall(faults);
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(100L), listeners = "logRetryListener", stateful = true)
    public void faultyCallStateful() {
      baseFaultyCallStateful();
    }
  }

  @Autowired
  FaultyService faultyService;

  @BeforeEach
  void clear() {
    faultyService.clear();
  }

  @Test
  void test_call_with_listener() {
    logStart();
    assertThatCode(() -> faultyService.faultyCall(2))
        .doesNotThrowAnyException();
    logEnd();
  }

  @Test
  void test_call_with_listener_and_stateful() {
    logStart();
    faultyService.getStatefulFaults().set(2);
    assertThatThrownBy(() -> faultyService.faultyCallStateful())
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> faultyService.faultyCallStateful())
        .isInstanceOf(IllegalArgumentException.class);
    assertThatCode(() -> faultyService.faultyCallStateful())
        .doesNotThrowAnyException();
    logEnd();
  }

  private void logStart() {
    log.debug("=== start ===");
  }

  private void logEnd() {
    log.debug("=== end ===");
  }

}
