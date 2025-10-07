package com.example.spring.batch.playground.features.resilience.api;

import com.example.spring.batch.playground.features.resilience.batch.JobParameterNames;
import com.example.spring.batch.playground.features.resilience.batch.ResilienceBatchConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(ResilienceBatchConfig.ENABLED_KEY)
@RequiredArgsConstructor
@Slf4j
public class ResilienceController {

  private final Job resilientJob;
  private final JobLauncher jobLauncher;
  private final JobExplorer jobExplorer;
  private final JobOperator jobOperator;

  public record ResilientJobRequest(String runId, String data, String failuresMap, Long backOffPeriod) {

  }

  public record ResilientJobActionRequest(Long id) {

  }

  @PostMapping("/jobs/resilience/resilientJob")
  public void resilientJob(@RequestBody ResilientJobRequest request) throws JobExecutionException {
    JobParameters jobParameters = new JobParameters(Map.of(
        JobParameterNames.RANDOM, new JobParameter<>(UUID.randomUUID().toString(), String.class),
        JobParameterNames.RUN_ID, new JobParameter<>(request.runId(), String.class),
        JobParameterNames.DATA, new JobParameter<>(request.data(), String.class),
        JobParameterNames.FAILURES_MAP, new JobParameter<>(request.failuresMap(), String.class),
        JobParameterNames.BACK_OFF_PERIOD, new JobParameter<>(request.backOffPeriod(), Long.class)
    ));
    jobLauncher.run(resilientJob, jobParameters);
  }

  @PostMapping("/jobs/resilience/resilientJob/restart")
  public void restartResilientJob(@RequestBody ResilientJobActionRequest request) throws JobExecutionException {
    JobInstance jobInstance = jobExplorer.getJobInstance(request.id());
    List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
    JobExecution jobExecution = jobExecutions.getFirst();
    jobOperator.restart(jobExecution.getId());
  }

  @PostMapping("/jobs/resilience/resilientJob/restartNewParams")
  public void restartNewParamsResilientJob(@RequestBody ResilientJobActionRequest request)
      throws JobExecutionException {
    JobInstance jobInstance = jobExplorer.getJobInstance(request.id());
    List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
    JobExecution jobExecution = jobExecutions.getFirst();

    var newParams = new HashMap<>(jobExecution.getJobParameters().getParameters());
    newParams.put(JobParameterNames.RE_RUN_ID, new JobParameter<>(UUID.randomUUID().toString(), String.class));
    JobParameters newJobParameters = new JobParameters(newParams);

    jobLauncher.run(resilientJob, newJobParameters);
  }

  @PostMapping("/jobs/resilience/resilientJob/stop")
  public void stopResilientJob(@RequestBody ResilientJobActionRequest request)
      throws JobExecutionException {
    JobInstance jobInstance = jobExplorer.getJobInstance(request.id());
    List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
    JobExecution jobExecution = jobExecutions.getFirst();

    jobOperator.stop(jobExecution.getId());
  }

}