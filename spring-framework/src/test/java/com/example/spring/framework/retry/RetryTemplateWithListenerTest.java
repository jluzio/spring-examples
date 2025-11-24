package com.example.spring.framework.retry;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.core.retry.Retryable;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.stereotype.Service;

@SpringBootTest
@Slf4j
class RetryTemplateWithListenerTest {

  @Configuration
  @EnableResilientMethods
  @Import({FaultyService.class, LogRetryListener.class})
  static class Config {

    @Bean
    LogRetryListener logRetryListener() {
      return new LogRetryListener();
    }

    @Bean
    RetryTemplate retryTemplate() {
      return new RetryTemplate(retryPolicy());
    }

    RetryPolicy retryPolicy() {
      return RetryPolicy.builder()
          .maxRetries(2)
          .delay(Duration.ofMillis(100))
          .build();
    }
  }

  @Slf4j
  public static class LogRetryListener implements RetryListener {

    @Override
    public void beforeRetry(RetryPolicy retryPolicy, Retryable<?> retryable) {
      log("beforeRetry",
          "retryPolicy", retryPolicy,
          "retryable", retryable
      );
    }

    @Override
    public void onRetrySuccess(RetryPolicy retryPolicy, Retryable<?> retryable, @Nullable Object result) {
      log("onRetrySuccess",
          "retryPolicy", retryPolicy,
          "retryable", retryable,
          "result", result
      );
    }

    @Override
    public void onRetryFailure(RetryPolicy retryPolicy, Retryable<?> retryable, Throwable throwable) {
      log("onRetrySuccess",
          "retryPolicy", retryPolicy,
          "retryable", retryable,
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

    public void baseFaultyCall(int faults) {
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
  @RequiredArgsConstructor
  public static class FaultyService extends BaseFaultyService {

    private final RetryTemplate retryTemplate;

    public void faultyCall(int faults) throws RetryException {
      retryTemplate.execute(() -> {
        baseFaultyCall(faults);
        return null;
      });
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

  private void logStart() {
    log.debug("=== start ===");
  }

  private void logEnd() {
    log.debug("=== end ===");
  }

}
