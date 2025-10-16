package com.example.spring.core.lib.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.spring.core.lib.resilience4j.Resilience4JTest.Config.ResilientService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.vavr.control.Try;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Future.State;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@ActiveProfiles("resilience4j")
@Slf4j
class Resilience4JTest {

  record TimedRun(String data, Duration executionTime) {

  }

  @TestConfiguration
  static class Config {

    @Component
    @Getter
    public static class ResilientService extends BaseService {

      private static final String TEST_SERVICE = "testService";

      private final AtomicInteger retryCallCounter = new AtomicInteger(0);
      private final AtomicReference<String> bulkheadData = new AtomicReference<>("");
      private final List<Instant> rateLimiterData = new ArrayList<>();

      @Retry(name = TEST_SERVICE)
      public String retry() {
        return retryCallCounter.incrementAndGet() == 3
            ? this.success() : this.failure();
      }

      @Retry(name = TEST_SERVICE)
      public String retry_ignoreException() {
        return this.ignoreException();
      }

      @Bulkhead(name = TEST_SERVICE)
      public String bulkhead_nonThreadSafeMethod(String data) {
        bulkheadData.set(data);
        return Mono.just(bulkheadData)
            .delayElement(Duration.ofMillis(200))
            .map(AtomicReference::get)
            .block();
      }

      @RateLimiter(name = TEST_SERVICE)
      public String rateLimiter(String data) {
        rateLimiterData.add(Instant.now());
        return Mono.just(data)
            .delayElement(Duration.ofMillis(100))
            .block();
      }
    }
  }

  @Autowired
  ResilientService service;

  @Test
  void test_retry() {
    var executionTime = runAndLog(() ->
        service.retry());
    assertThat(service.getRetryCallCounter().get())
        .isEqualTo(3);
    assertThat(executionTime)
        .isBetween(Duration.ofMillis(600), Duration.ofMillis(800));
  }

  @Test
  void test_retry_ignoreException() {
    var executionTime = runAndLog(() ->
        assertThatThrownBy(() -> service.retry_ignoreException()).isInstanceOf(BusinessException.class));
    assertThat(executionTime)
        .isLessThan(Duration.ofMillis(200));
  }

  @Test
  void test_bulkhead() throws InterruptedException, ExecutionException {
    var start = Instant.now();
    IntFunction<Callable<TimedRun>> toCallable = id -> () -> {
      log.debug("starting :: {}", id);
      log.debug("calling func :: {}", id);
      var data = service.bulkhead_nonThreadSafeMethod("data" + id);
      log.debug("completed func :: {}", id);
      return new TimedRun(data, Duration.between(start, Instant.now()));
    };

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      var callables = IntStream.rangeClosed(1, 3)
          .mapToObj(toCallable)
          .toList();

      var futures = executor.invokeAll(callables);
      log.debug("futures: {}", futures.stream().map(Future::state).toList());

      var callsByState = futures.stream()
          .collect(Collectors.groupingBy(Future::state));
      assertThat(callsByState.get(State.SUCCESS))
          .hasSize(2);
      assertThat(callsByState.get(State.FAILED))
          .hasSize(1);

      var successfulCallsOrderedByExecutionTime = futures.stream()
          .filter(f -> f.state() == State.SUCCESS)
          .map(f -> Try.of(f::get).get())
          .sorted(Comparator.comparing(TimedRun::executionTime))
          .toList();

      // note that successful calls are probably not in the initial order
      log.debug("successfulCallsOrderedByExecutionTime: {}", successfulCallsOrderedByExecutionTime);

      var successfulCall1 = successfulCallsOrderedByExecutionTime.get(0);
      assertThat(successfulCall1.executionTime())
          .isBetween(Duration.ofMillis(200), Duration.ofMillis(350));

      var successfulCall2 = successfulCallsOrderedByExecutionTime.get(1);
      assertThat(successfulCall2.executionTime())
          .isGreaterThan(successfulCall1.executionTime())
          .isBetween(Duration.ofMillis(400), Duration.ofMillis(550));
    }
  }

  @Test
  void test_rateLimiter() throws InterruptedException, ExecutionException {
    var start = Instant.now();
    // forcing execution order due to initial delay, for easier test assertions
    IntFunction<Callable<TimedRun>> toCallable = id -> () -> {
      log.debug("starting :: {}", id);
      Mono.delay(Duration.ofMillis(10L * id)).block();
      log.debug("calling func :: {}", id);
      var data = service.rateLimiter("data" + id);
      log.debug("completed func :: {}", id);
      Mono.delay(Duration.ofMillis(50L - 10L * id)).block();
      return new TimedRun(data, Duration.between(start, Instant.now()));
    };

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      var callables = IntStream.rangeClosed(1, 3)
          .mapToObj(toCallable)
          .toList();
      var futures = executor.invokeAll(callables);
      log.debug("futures: {}", futures.stream().map(Future::state).toList());
      log.debug("futures.data: {}", futures.stream().map(f -> Try.of(f::get).get()).toList());

      var data1Call = futures.get(0);
      assertThat(data1Call.state())
          .isEqualTo(State.SUCCESS);
      var data1TimedRun = data1Call.get();
      assertThat(data1TimedRun.data())
          .isEqualTo("data1");
      assertThat(data1TimedRun.executionTime())
          .isBetween(Duration.ofMillis(150), Duration.ofMillis(200));

      var data2Call = futures.get(1);
      assertThat(data2Call.state())
          .isEqualTo(State.SUCCESS);
      var data2TimedRun = data2Call.get();
      assertThat(data2TimedRun.data())
          .isEqualTo("data2");
      assertThat(data2TimedRun.executionTime())
          .isBetween(Duration.ofMillis(150), Duration.ofMillis(200));

      var data3Call = futures.get(2);
      assertThat(data3Call.state())
          .isEqualTo(State.SUCCESS);
      var data3TimedRun = data3Call.get();
      assertThat(data3TimedRun.data())
          .isEqualTo("data3");
      // unsure on how much time it will take, since the window might refresh right after
      assertThat(data3TimedRun.executionTime())
          .isGreaterThan(data1TimedRun.executionTime())
          .isGreaterThan(data2TimedRun.executionTime());
    }
  }

  private Duration runAndLog(Runnable runnable) {
    var start = Instant.now();
    runnable.run();
    var executionTime = Duration.between(start, Instant.now());
    log.debug("executionTime: {}", executionTime);
    return executionTime;
  }

}
