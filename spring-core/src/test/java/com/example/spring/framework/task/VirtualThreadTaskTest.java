package com.example.spring.framework.task;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@SpringBootTest(
    properties = "spring.threads.virtual.enabled=true"
)
@Slf4j
class VirtualThreadTaskTest {

  @Configuration
  @EnableAsync
  @Import({TaskLauncher.class, TaskExecutionAutoConfiguration.class})
  static class Config {

  }

  @Component
  static class TaskLauncher {

    @Async
    Future<String> taskReturnThreadId() {
      return CompletableFuture.completedFuture(
          Thread.currentThread().toString());
    }
  }

  @Autowired
  TaskLauncher taskLauncher;

  @Test
  void test() {
    var result = taskLauncher.taskReturnThreadId();
    log.debug("{}", result);
    assertThat(result)
        .succeedsWithin(1, TimeUnit.SECONDS, InstanceOfAssertFactories.STRING)
        .satisfies(log::debug)
        .contains("Virtual");
  }

}
