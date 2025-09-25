package com.example.spring.batch.playground.features.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBatchTest
@SpringJUnitConfig(HelloWorldJobFunctionalTests.JobConfiguration.class)
@EnableAutoConfiguration
class HelloWorldJobFunctionalTests {

  static class JobConfiguration {

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
      return new StepBuilder("step", jobRepository)
          .tasklet(
              (contribution, chunkContext) -> {
                IO.println("Hello world!");
                return RepeatStatus.FINISHED;
              },
              transactionManager
          )
          .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
      return new JobBuilder("job", jobRepository)
          .start(step)
          .build();
    }
  }

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Test
  void testJob() throws Exception {
    var jobParameters = jobLauncherTestUtils.getUniqueJobParameters();

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
  }
}
