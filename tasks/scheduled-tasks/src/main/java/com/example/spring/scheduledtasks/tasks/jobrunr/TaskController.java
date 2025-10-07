package com.example.spring.scheduledtasks.tasks.jobrunr;

import com.example.spring.scheduledtasks.tasks.model.GenericData;
import java.time.Instant;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks/jobrunr")
@ConditionalOnProperty({TaskConfig.ENABLED_KEY})
@RequiredArgsConstructor
public class TaskController {

  private final JobScheduler scheduler;
  private final Function<GenericData, Void> logDataLambda;

  @PostMapping("/logData")
  public void logData(@RequestBody GenericData data) {
    scheduler.schedule(
        Instant.now().plusSeconds(10),
        () -> logDataLambda.apply(data));
  }

}
