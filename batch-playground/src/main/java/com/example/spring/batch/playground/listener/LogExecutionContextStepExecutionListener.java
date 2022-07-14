package com.example.spring.batch.playground.listener;

import static com.example.spring.batch.playground.util.LogExecutionContextHelper.logData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@RequiredArgsConstructor
@Slf4j
public class LogExecutionContextStepExecutionListener implements StepExecutionListener {

  private final boolean logJobExecution;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    logData(stepExecution, stepExecution.getExecutionContext(), "beforeStep");
    if (logJobExecution) {
      var jobExecution = stepExecution.getJobExecution();
      logData(jobExecution, jobExecution.getExecutionContext(), "beforeStep");
    }
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    logData(stepExecution, stepExecution.getExecutionContext(), "afterStep");
    if (logJobExecution) {
      var jobExecution = stepExecution.getJobExecution();
      logData(jobExecution, jobExecution.getExecutionContext(), "afterStep");
    }
    return stepExecution.getExitStatus();
  }
}
