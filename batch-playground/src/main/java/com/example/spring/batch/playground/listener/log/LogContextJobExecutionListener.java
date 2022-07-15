package com.example.spring.batch.playground.listener.log;

import static com.example.spring.batch.playground.listener.log.LogContextHelper.logContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@RequiredArgsConstructor
@Slf4j
public class LogContextJobExecutionListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    logContext(log, jobExecution, "beforeJob");
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    logContext(log, jobExecution, "afterJob");
  }
}
