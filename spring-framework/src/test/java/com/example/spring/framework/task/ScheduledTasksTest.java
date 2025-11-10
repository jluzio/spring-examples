package com.example.spring.framework.task;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import com.example.spring.framework.task.ScheduledTasksTest.ScheduledTasks;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(classes = ScheduledTasks.class)
@EnableScheduling
@Slf4j
class ScheduledTasksTest {

  @Component
  static class ScheduledTasks {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Scheduled(fixedRate = 750)
    void reportCurrentTime() {
      log.info("The time is now {}", dateTimeFormatter.format(LocalDateTime.now()));
    }

  }

  @MockitoSpyBean
  ScheduledTasks tasks;

  @Test
  void test() {
    await().atMost(Durations.TWO_SECONDS).untilAsserted(() -> {
      verify(tasks, atLeast(2)).reportCurrentTime();
    });
  }

}
