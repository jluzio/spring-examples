package com.example.spring.batch.playground.batch.listener.log;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

@UtilityClass
public class LogContextHelper {

  public static final String LOG_FORMAT = "{}[{}] :: Context={} | Execution=({}) | ParentExecution={}";

  public static void logContext(Logger log, JobExecution jobExecution, String tag) {
    logContext(log, jobExecution, tag, jobExecution.getExecutionContext(), null);
  }

  public static void logContext(Logger log, StepExecution stepExecution, String tag) {
    logContext(log, stepExecution, tag, stepExecution.getExecutionContext(),
        stepExecution.getJobExecution().getJobId());
  }

  private static void logContext(Logger log, Object execution, String tag,
      ExecutionContext executionContext, Object parentExecution) {
    log.debug(LOG_FORMAT,
        execution.getClass().getSimpleName(),
        tag,
        executionContext,
        execution,
        parentExecution);
  }
}
