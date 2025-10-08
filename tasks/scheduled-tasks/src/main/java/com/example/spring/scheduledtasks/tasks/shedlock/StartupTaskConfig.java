package com.example.spring.scheduledtasks.tasks.shedlock;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@ConditionalOnProperty({TaskConfig.ENABLED_KEY, TaskConfig.STARTUP_TASKS_ENABLED_KEY})
@Slf4j
public class StartupTaskConfig {

  @Scheduled(initialDelay = 5_000)
  @SchedulerLock(name = "logHello")
  public void logHello() {
    // To assert that the lock is held (prevents misconfiguration errors)
    LockAssert.assertLocked();
    
    log.debug("logHello");
  }

}
