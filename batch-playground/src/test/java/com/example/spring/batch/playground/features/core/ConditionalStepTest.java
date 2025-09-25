package com.example.spring.batch.playground.features.core;

import static org.assertj.core.api.Assertions.assertThat;
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
@SpringJUnitConfig(ConditionalStepTest.JobConfiguration.class)
@EnableAutoConfiguration
class ConditionalStepTest {

  public static final String OUTPUT_ATTR = "output";

  static class JobConfiguration {

    @Bean
    public Step stepA(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
      return new StepBuilder("stepA", jobRepository)
          .tasklet(
              (contribution, chunkContext) -> {
                throw new IllegalStateException("Some exception");
              },
              transactionManager
          )
          .build();
    }

    @Bean
    public Step stepB(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
      return new StepBuilder("stepB", jobRepository)
          .tasklet(
              (contribution, chunkContext) -> {
                contribution.getStepExecution().getJobExecution().getExecutionContext()
                    .put(OUTPUT_ATTR, "stepB");
                return RepeatStatus.FINISHED;
              },
              transactionManager
          )
          .build();
    }

    @Bean
    public Step stepC(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
      return new StepBuilder("stepC", jobRepository)
          .tasklet(
              (contribution, chunkContext) -> {
                contribution.getStepExecution().getJobExecution().getExecutionContext()
                    .put(OUTPUT_ATTR, "stepC");
                return RepeatStatus.FINISHED;
              },
              transactionManager
          )
          .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step stepA, Step stepB, Step stepC) {
      return new JobBuilder("job", jobRepository)
          .start(stepA)
          .on("*").to(stepB)
          .from(stepA).on("FAILED").to(stepC)
          .end()
          .build();
    }
  }

  @Autowired
  JobLauncherTestUtils jobLauncherTestUtils;

  @Test
  void testJob() throws Exception {
    var jobParameters = jobLauncherTestUtils.getUniqueJobParameters();

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    assertThat(jobExecution.getExecutionContext().get(OUTPUT_ATTR))
        .isEqualTo("stepC");
  }
}
