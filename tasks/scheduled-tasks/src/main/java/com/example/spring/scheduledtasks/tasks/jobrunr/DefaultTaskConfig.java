package com.example.spring.scheduledtasks.tasks.jobrunr;

import com.example.spring.scheduledtasks.tasks.model.GenericData;
import com.github.kagkarlsson.scheduler.task.TaskDescriptor;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty({TaskConfig.ENABLED_KEY})
@Slf4j
public class DefaultTaskConfig {

  public static final TaskDescriptor<GenericData> LOG_DATA_TASK =
      TaskDescriptor.of("log-data", GenericData.class);

  @Bean
  public Function<GenericData, Void> logDataLambda() {
    return data -> {
      log.debug("LogDataJob :: {}", data);
      return null;
    };
  }
}
