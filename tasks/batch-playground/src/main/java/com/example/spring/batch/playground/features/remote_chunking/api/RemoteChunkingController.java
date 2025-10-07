package com.example.spring.batch.playground.features.remote_chunking.api;

import com.example.spring.batch.playground.features.remote_chunking.batch.JobParameters;
import com.example.spring.batch.playground.features.remote_chunking.batch.RemoteChunkingBatchConfig;
import com.example.spring.batch.playground.mapper.JobMapper;
import com.example.spring.batch.playground.model.JobStatusResponse;
import java.util.Map;
import java.util.UUID;
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
@RequestMapping("/jobs/remote-chunking")
@RequiredArgsConstructor
@ConditionalOnProperty(RemoteChunkingBatchConfig.ENABLED_KEY)
public class RemoteChunkingController {

  private final JobLauncher jobLauncher;
  private final Job remoteChunkingJob;
  private final JobMapper jobMapper;

  public record RemoteChunkingJobRequest(String runId, String data) {

  }

  @PostMapping("/simple")
  public JobStatusResponse simpleJob(@RequestBody RemoteChunkingJobRequest request)
      throws JobExecutionException {
    var jobParameters = new org.springframework.batch.core.JobParameters(Map.of(
        JobParameters.RUN_ID, new JobParameter<>(request.runId(), String.class),
        JobParameters.DATA, new JobParameter<>(request.data(), String.class),
        "random", new JobParameter<>(UUID.randomUUID().toString(), String.class)
    ));

    var jobExecution = jobLauncher.run(remoteChunkingJob, jobParameters);

    return jobMapper.jobStatusResponse(jobExecution);
  }

}
