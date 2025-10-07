package com.example.spring.scheduledtasks.tasks.quartz;

import com.example.spring.scheduledtasks.tasks.model.GenericData;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks/quartz")
@ConditionalOnProperty({TaskConfig.ENABLED_KEY})
@RequiredArgsConstructor
public class TaskController {

  private final Scheduler scheduler;
  private final JobDetail logDataJobDetail;

  @PostMapping("/logData")
  public void logData(@RequestBody GenericData data) throws SchedulerException {
    var jobDataMap = new JobDataMap();
    jobDataMap.put("data", data);
    var trigger = TriggerBuilder.newTrigger()
        .forJob(logDataJobDetail)
        .withIdentity("logDataTrigger")
        .usingJobData(jobDataMap)
        .startAt(Date.from(Instant.now().plusSeconds(10)))
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withMisfireHandlingInstructionFireNow()
        )
        .build();
    scheduler.scheduleJob(trigger);
  }

}
