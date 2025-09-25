package com.example.spring.batch.playground.features.user.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.spring.batch.playground.BaseSpringBatchTestConfig;
import com.example.spring.batch.playground.features.user.persistence.model.Post;
import com.example.spring.batch.playground.features.user.persistence.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBatchTest
@SpringJUnitConfig({UserJobSpringBatchTest.JobConfiguration.class, BaseSpringBatchTestConfig.class})
class UserJobSpringBatchTest {

  @Import({ItemIOConfig.class, UserEnsureIdItemProcessor.class, PostFilterByUserItemProcessor.class,
      JobCompletionNotificationListener.class})
  static class JobConfiguration {

    @Bean
    public Job importUserJob(
        JobRepository jobRepository,
        JobCompletionNotificationListener listener,
        Step userStep,
        Step postStep
    ) {
      return new JobBuilder("importUserJob", jobRepository)
          .listener(listener)
          .start(userStep)
          .next(postStep)
          .build();
    }

    @Bean
    public Step userStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        @Qualifier("userSampleDataCsvItemReader") ItemReader<User> reader,
        UserEnsureIdItemProcessor processor,
        @Qualifier("userRepositoryItemWriter") ItemWriter<User> writer
    ) {
      return new StepBuilder("userStep", jobRepository)
          .<User, User>chunk(10, transactionManager)
          .reader(reader)
          .processor(processor)
          .writer(writer)
          .build();
    }

    @Bean
    public Step postStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        @Qualifier("postExternalApiItemReader") ItemReader<Post> reader,
        PostFilterByUserItemProcessor processor,
        @Qualifier("postRepositoryItemWriter") ItemWriter<Post> writer) {
      return new StepBuilder("postStep", jobRepository)
          .<Post, Post>chunk(10, transactionManager)
          .reader(reader)
          .processor(processor)
          .writer(writer)
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
