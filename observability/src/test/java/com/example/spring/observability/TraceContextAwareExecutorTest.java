package com.example.spring.observability;

import io.micrometer.tracing.Tracer;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootTest
@Slf4j
class TraceContextAwareExecutorTest {

  @TestConfiguration
  @EnableAsync
  @Import({SomeAsyncService.class})
  static class Config {

    @Bean(name = "tracingExecutor")
    public Executor tracingExecutor(Tracer tracer) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(2);
      executor.setMaxPoolSize(5);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("tracing-");
      executor.initialize();
      return new TraceContextAwareExecutor(executor, tracer);
    }
  }

  static class SomeAsyncService {
    @Async
    public void execute() {
      log.info("SomeAsyncService.execute()");
    }

    @Async("tracingExecutor")
    public void executeWithTracingExecutor() {
      log.info("SomeAsyncService.execute()");
    }
  }

  @Autowired
  Tracer tracer;
  @Autowired
  SomeAsyncService someAsyncService;

  @Test
  void test() {
    tracer.startScopedSpan("test");
    log.info("test");
    someAsyncService.execute();
    someAsyncService.executeWithTracingExecutor();
  }
}