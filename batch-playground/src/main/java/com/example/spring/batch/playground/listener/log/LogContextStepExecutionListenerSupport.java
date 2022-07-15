package com.example.spring.batch.playground.listener.log;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class LogContextStepExecutionListenerSupport implements StepExecutionListener {

  protected StepExecution stepExecution;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    this.stepExecution = null;
    return stepExecution.getExitStatus();
  }
}
