package com.example.spring.scheduledtasks.tasks.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConditionalOnProperty({TaskConfig.ENABLED_KEY})
@Slf4j
public class DefaultTaskConfig {

  enum TriggerType {SINGLE, CRON}

  @Value("${app.tasks.quartz.startup-tasks.helloWorld.trigger-type:SINGLE}")
  private TriggerType helloWorldTriggerType;

  @Component
  public static class LogDataJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
      Object data = context.getMergedJobDataMap().get("data");
      log.debug("LogDataJob :: {}", data);
    }
  }

  @Bean
  public JobDetail logDataJobDetail() {
    return JobBuilder.newJob(LogDataJob.class)
        .withIdentity("logDataJob")
        .storeDurably()
        .build();
  }

}
