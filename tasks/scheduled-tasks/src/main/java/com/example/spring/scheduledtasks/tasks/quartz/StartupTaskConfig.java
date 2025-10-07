package com.example.spring.scheduledtasks.tasks.quartz;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConditionalOnProperty({TaskConfig.ENABLED_KEY, TaskConfig.STARTUP_TASKS_ENABLED_KEY})
@Slf4j
public class StartupTaskConfig {

  enum TriggerType {SINGLE, CRON}

  @Value("${app.tasks.quartz.startup-tasks.helloWorld.trigger-type:SINGLE}")
  private TriggerType helloWorldTriggerType;

  @Component
  public static class HelloWorldJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
      log.debug("hello!! (Quartz JDBC)");
    }
  }

  @Bean
  public JobDetail helloWorldJobDetail() {
    return JobBuilder.newJob(HelloWorldJob.class)
        .withIdentity("helloWorldJob")
        .storeDurably()
        .build();
  }

  @Bean
  @SuppressWarnings("java:S1301")
  public Trigger helloWorldJobTrigger(JobDetail helloWorldJobDetail) {
    var helloWorldTrigger = TriggerBuilder.newTrigger()
        .forJob(helloWorldJobDetail)
        .withIdentity("helloWorldTrigger");
    switch (helloWorldTriggerType) {
      case SINGLE -> helloWorldTrigger.startAt(Date.from(Instant.now().plusSeconds(5)));
      case CRON -> helloWorldTrigger.withSchedule(CronScheduleBuilder.cronSchedule("0 * * ? * MON-FRI"));
    }
    return helloWorldTrigger
        .build();
  }

}
