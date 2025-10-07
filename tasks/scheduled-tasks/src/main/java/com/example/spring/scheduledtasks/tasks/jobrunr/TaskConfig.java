package com.example.spring.scheduledtasks.tasks.jobrunr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(TaskConfig.ENABLED_KEY)
@SuppressWarnings("java:S1118")
@Slf4j
public class TaskConfig {

  public static final String ENABLED_KEY = "app.tasks.jobrunr.enabled";
  public static final String STARTUP_TASKS_ENABLED_KEY = "app.tasks.jobrunr.startup-tasks.enabled";

}
