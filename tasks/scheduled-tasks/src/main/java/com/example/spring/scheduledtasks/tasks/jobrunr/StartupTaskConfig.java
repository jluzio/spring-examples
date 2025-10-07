package com.example.spring.scheduledtasks.tasks.jobrunr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty({TaskConfig.ENABLED_KEY, TaskConfig.STARTUP_TASKS_ENABLED_KEY})
@Slf4j
public class StartupTaskConfig {

}
