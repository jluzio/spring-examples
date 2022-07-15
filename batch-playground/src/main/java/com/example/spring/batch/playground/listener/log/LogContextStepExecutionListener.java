package com.example.spring.batch.playground.listener.log;

import static com.example.spring.batch.playground.listener.log.LogContextHelper.logContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class LogContextStepExecutionListener implements StepExecutionListener {

  @Override
  public void beforeStep(StepExecution stepExecution) {
    logContext(log, stepExecution, "beforeStep");
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    logContext(log, stepExecution, "afterStep");
    return stepExecution.getExitStatus();
  }
}
