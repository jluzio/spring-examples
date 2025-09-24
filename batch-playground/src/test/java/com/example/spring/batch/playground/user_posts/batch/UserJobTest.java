package com.example.spring.batch.playground.user_posts.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.batch.playground.user_posts.persistence.model.Post;
import com.example.spring.batch.playground.user_posts.persistence.model.User;
import com.example.spring.batch.playground.user_posts.persistence.repository.UserRepository;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
@Slf4j
class UserJobTest {

  @TestConfiguration
  static class Config {

    @Configuration
    static class BatchConfig {

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
  }

  @Autowired
  UserRepository userRepository;
  @Autowired
  Job importUserJob;
  @Autowired
  JobLauncher jobLauncher;

  @Test
  void test() {
    var users = toList(userRepository.findAll());
    log.info("{}", users);
    assertThat(users).isEmpty();

    log.info("{}", importUserJob);
    var jobParameters = new JobParameters();
    try {
      var execution = jobLauncher.run(importUserJob, jobParameters);
      assertThat(execution.getStatus())
          .isEqualTo(BatchStatus.COMPLETED);

      var finalUsers = toList(userRepository.findAll());
      log.info("{}", finalUsers);
      assertThat(finalUsers).hasSize(5);
    } catch (JobExecutionAlreadyRunningException | JobRestartException |
             JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
      throw new RuntimeException(e);
    }
  }

  <T> List<T> toList(Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false)
        .toList();
  }
}
