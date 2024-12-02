package com.example.spring.core.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
@Slf4j
class ConcurrencyEventTest {

  @Configuration
  @EnableAsync
  @Import({MyAsyncUncaughtExceptionHandler.class, Listeners.class})
  static class Config implements AsyncConfigurer {

    @SpyBean
    MyAsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler;

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
      return asyncUncaughtExceptionHandler;
    }
  }

  @RequiredArgsConstructor
  static class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void handleUncaughtException(Throwable e, Method method, Object... params) {
      log.info("Caught exception!!", e);
      eventPublisher.publishEvent(new DoneEvent("error"));
    }
  }

  @RequiredArgsConstructor
  static class Listeners {

    private final ApplicationEventPublisher eventPublisher;

    @EventListener(value = StartEvent.class, condition = "#event.data == 'processSyncSuccess'")
    void processSyncSuccess(Object event) {
      log.info("processSyncSuccess: {}", event);
      eventPublisher.publishEvent(new DoneEvent("processSyncSuccess"));
    }

    @EventListener(value = StartEvent.class, condition = "#event.data == 'processSyncThrowError'")
    void processSyncThrowError(Object event) {
      log.info("processSyncThrowError: {}", event);
      throw new IllegalArgumentException("some error");
    }

    @EventListener(value = StartEvent.class, condition = "#event.data == 'processAsyncSuccess'")
    @Async
    void processAsyncSuccess(Object event) {
      log.info("processAsyncSuccess: {}", event);
      eventPublisher.publishEvent(new DoneEvent("processAsync"));
    }

    @EventListener(value = StartEvent.class, condition = "#event.data == 'processAsyncThrowError'")
    @Async
    void processAsyncThrowError(Object event) {
      log.info("processAsyncThrowError: {}", event);
      throw new IllegalArgumentException("some error");
    }
  }

  record StartEvent(String data) {

  }

  record DoneEvent(String data) {

  }

  @Autowired
  ApplicationEventPublisher eventPublisher;
  @Autowired
  ApplicationEvents applicationEvents;
  @SpyBean
  Listeners listeners;
  @SpyBean
  AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler;

  @Test
  void processSyncSuccess() {
    eventPublisher.publishEvent(new StartEvent("processSyncSuccess"));
    assertThat(applicationEvents.stream(StartEvent.class).count())
        .isOne();

    verify(listeners).processSyncSuccess(any());
  }

  @Test
  void processSyncThrowError() {
    assertThatThrownBy(() -> eventPublisher.publishEvent(new StartEvent("processSyncThrowError")))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(applicationEvents.stream(StartEvent.class).count())
        .isZero();

    verify(listeners).processSyncThrowError(any());
  }

  @Test
  void processAsyncSuccess() {
    eventPublisher.publishEvent(new StartEvent("processAsyncSuccess"));
    assertThat(applicationEvents.stream(StartEvent.class).count())
        .isOne();

    awaitUntilDone();
    verify(listeners).processAsyncSuccess(any());
    verify(asyncUncaughtExceptionHandler, never()).handleUncaughtException(any(), any(), any());
  }

  @Test
  void processAsyncThrowError() {
    eventPublisher.publishEvent(new StartEvent("processAsyncThrowError"));
    assertThat(applicationEvents.stream(StartEvent.class).count())
        .isOne();

    awaitUntilDone();
    verify(listeners).processAsyncThrowError(any());
    verify(asyncUncaughtExceptionHandler).handleUncaughtException(any(), any(), any());
  }

  void awaitUntilDone() {
    await()
        .atMost(Duration.ofMillis(200))
        .until(() -> applicationEvents.stream(DoneEvent.class).count() == 1);
  }

}
