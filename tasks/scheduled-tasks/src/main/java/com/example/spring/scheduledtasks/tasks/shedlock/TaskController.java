package com.example.spring.scheduledtasks.tasks.shedlock;

import com.example.spring.scheduledtasks.tasks.model.GenericData;
import com.github.kagkarlsson.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks/shedlock")
@ConditionalOnProperty({TaskConfig.ENABLED_KEY})
@RequiredArgsConstructor
public class TaskController {

  private final Scheduler scheduler;

  @PostMapping("/logData")
  public void logData(@RequestBody GenericData data) {

  }

}
