package com.example.spring.batch.playground.features.user.api;

import com.example.spring.batch.playground.batch.JobService;
import com.example.spring.batch.playground.mapper.JobMapper;
import com.example.spring.batch.playground.model.JobStatusResponse;
import com.example.spring.batch.playground.features.user.api.model.ImportCsvFileJobRequest;
import com.example.spring.batch.playground.features.user.api.model.ImportDataJobRequest;
import com.example.spring.batch.playground.features.user.batch.JobParameters;
import com.example.spring.batch.playground.features.user.config.UserBatchConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs/users")
@RequiredArgsConstructor
@ConditionalOnProperty(UserBatchConfig.JOBS_ENABLED_KEY)
public class UserJobController {

  private final JobLauncher jobLauncher;
  private final Job userImportCsvFileJob;
  private final Job userImportDataJob;
  private final JobMapper jobMapper;
  private final ObjectMapper objectMapper;
  private final JobService jobService;

  @PostMapping("/import/csv")
  public JobStatusResponse importCsvFileJob(@RequestBody ImportCsvFileJobRequest request) throws JobExecutionException {
    var jobParameters = new org.springframework.batch.core.JobParameters(Map.of(
        JobParameters.RUN_ID, new JobParameter<>(request.runId(), String.class)
    ));

    var jobExecution = getJobLauncher(request)
        .run(userImportCsvFileJob, jobParameters);

    return jobMapper.jobStatusResponse(jobExecution);
  }

  @PostMapping("/import/data")
  public JobStatusResponse importDataJob(@RequestBody ImportDataJobRequest request)
      throws JobExecutionException, JsonProcessingException {
    var inputUsersJson = objectMapper.writeValueAsString(request.users());
    var jobParameters = new org.springframework.batch.core.JobParameters(Map.of(
        JobParameters.RUN_ID, new JobParameter<>(request.runId(), String.class),
        JobParameters.INPUT_USERS, new JobParameter<>(inputUsersJson, String.class)
    ));


    var jobExecution = getJobLauncher(request)
        .run(userImportDataJob, jobParameters);

    return jobMapper.jobStatusResponse(jobExecution);
  }

  private JobLauncher getJobLauncher(Object request) {
    boolean async = switch (request) {
      case ImportCsvFileJobRequest r -> r.async();
      case ImportDataJobRequest r -> r.async();
      default -> false;
    };
    return async
        ? jobService.asyncJobLauncherInstance()
        : jobLauncher;
  }

}
