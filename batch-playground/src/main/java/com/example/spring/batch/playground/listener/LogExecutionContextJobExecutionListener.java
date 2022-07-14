package com.example.spring.batch.playground.listener;

import static com.example.spring.batch.playground.util.LogExecutionContextHelper.logData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@RequiredArgsConstructor
@Slf4j
public class LogExecutionContextJobExecutionListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    logData(jobExecution, jobExecution.getExecutionContext(), "beforeJob");
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    logData(jobExecution, jobExecution.getExecutionContext(), "afterJob");
  }
}
