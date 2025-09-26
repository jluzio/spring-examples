package com.example.spring.batch.playground.scheduler;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Configuration
@EnableScheduling
@ConditionalOnProperty("app.features.scheduled-tasks.enabled")
public class ScheduledTasksConfig {

  @Component
  public class MyQuartzJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
      System.out.println("hello!! (Quartz JDBC)");
    }
  }

  @Bean
  public JobDetail myQuartzJobDetail() {
    return JobBuilder.newJob(MyQuartzJob.class)
        .withIdentity("myQuartzJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger myQuartzJobTrigger(JobDetail myQuartzJobDetail) {
    return TriggerBuilder.newTrigger()
        .forJob(myQuartzJobDetail)
        .withIdentity("myQuartzTrigger")
        .withSchedule(CronScheduleBuilder.cronSchedule("0 * * ? * MON-FRI"))
        .build();
  }

}
