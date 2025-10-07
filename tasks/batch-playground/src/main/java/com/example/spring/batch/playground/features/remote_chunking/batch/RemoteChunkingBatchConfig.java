package com.example.spring.batch.playground.features.remote_chunking.batch;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableBatchIntegration
@Import({ManagerConfig.class, WorkerConfig.class})
@ConditionalOnProperty(RemoteChunkingBatchConfig.ENABLED_KEY)
@Slf4j
public class RemoteChunkingBatchConfig {

  public static final String BASE_KEY = "app.features.remote_chunking";
  public static final String ENABLED_KEY = BASE_KEY + ".enabled";
  public static final String MANAGER_ENABLED_KEY = BASE_KEY + ".manager-enabled";
  public static final String WORKER_ENABLED_KEY = BASE_KEY + ".worker-enabled";
  public static final String RESTART_FAILED_JOBS_ENABLED_KEY = BASE_KEY + ".restart-failed-jobs-enabled";

  @Bean
  Job remoteChunkingJob(JobRepository jobRepository, Step managerStep) {
    return new JobBuilder("remoteChunkingJob", jobRepository)
        .start(managerStep)
        .build();
  }

  @Bean
  public org.apache.activemq.ActiveMQConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
    factory.setBrokerURL("tcp://localhost:61616");
    factory.setUserName("artemis");
    factory.setPassword("artemis");
    factory.setTrustAllPackages(true);
    return factory;
  }

  @Bean
  @ConditionalOnProperty(RESTART_FAILED_JOBS_ENABLED_KEY)
  CommandLineRunner restartFailedJobs(
      JobExplorer jobExplorer,
      JobLauncher jobLauncher,
      JobOperator jobOperator
  ) {
    return args -> {
      List<JobInstance> jobs = jobExplorer.findJobInstancesByJobName("remoteChunkingJob", 0, 100);
      jobs.forEach(job -> {
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(job);
        JobExecution jobExecution = jobExecutions.getFirst();
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
          try {
            jobOperator.restart(jobExecution.getId());
          } catch (JobInstanceAlreadyCompleteException | NoSuchJobExecutionException | NoSuchJobException |
                   JobRestartException | JobParametersInvalidException e) {
            log.error("Unable to restart job '{}' :: {}", job, e.getMessage());
          }
        }
      });
    };
  }

}
