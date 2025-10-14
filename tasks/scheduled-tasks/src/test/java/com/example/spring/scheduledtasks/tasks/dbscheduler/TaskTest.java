package com.example.spring.scheduledtasks.tasks.dbscheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.boot.autoconfigure.DbSchedulerAutoConfiguration;
import com.github.kagkarlsson.scheduler.task.TaskDescriptor;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {
    "db-scheduler.polling-interval=PT0.5S"
})
@Slf4j
class TaskTest {

  public static final TaskDescriptor<String> TEST_TASK =
      TaskDescriptor.of("test-task", String.class);

  static final AtomicBoolean DONE_FLAG = new AtomicBoolean(false);

  @Configuration
  @Import({DataSourceAutoConfiguration.class, FlywayAutoConfiguration.class, DbSchedulerAutoConfiguration.class})
  static class Config {

    @Bean
    public OneTimeTask<String> sendEventJob() {
      return Tasks.oneTime(TEST_TASK)
          .execute((inst, ctx) -> {
            var data = inst.getData();
            log.info("{} :: {}", inst.getTaskName(), data);
            DONE_FLAG.set(true);
          });
    }
  }

  @Autowired
  DataSource dataSource;
  @Autowired
  Scheduler scheduler;

  @Test
  void test() {
    assertThat(dataSource).isNotNull();
    log.debug("{}", dataSource);

    assertThat(scheduler).isNotNull();
    assertThat(scheduler.getCurrentlyExecuting())
        .isEmpty();

    log.info("getScheduledExecutions: {}", scheduler.getScheduledExecutions());

    scheduler.schedule(TEST_TASK
        .instance("test")
        .data("test")
        .scheduledTo(Instant.now().plusMillis(200))
    );

    log.info("getScheduledExecutions: {}", scheduler.getScheduledExecutions());

    await()
        .atMost(2, TimeUnit.SECONDS)
        .pollInterval(500, TimeUnit.MILLISECONDS)
        .pollInSameThread()
        .untilTrue(DONE_FLAG);
  }

}
