package com.example.spring.batch.playground.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;

@UtilityClass
@Slf4j
public class LogExecutionContextHelper {

  public static void logData(Object execution, ExecutionContext executionContext, String tag) {
    log.info("{}[{}] :: Context = {} | Execution = {}",
        execution.getClass().getSimpleName(),
        tag,
        executionContext,
        execution);
  }
}
