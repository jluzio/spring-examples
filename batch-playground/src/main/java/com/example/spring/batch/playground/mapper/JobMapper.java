package com.example.spring.batch.playground.mapper;

import com.example.spring.batch.playground.model.JobStatusResponse;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

  public JobStatusResponse jobStatusResponse(JobExecution jobExecution) {
    return JobStatusResponse.builder()
        .jobId(jobExecution.getJobId())
        .jobParameters(jobExecution.getJobParameters())
        .status(jobExecution.getStatus())
        .createTime(jobExecution.getCreateTime())
        .startTime(jobExecution.getStartTime())
        .endTime(jobExecution.getEndTime())
        .build();
  }

}
