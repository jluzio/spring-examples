package com.example.spring.batch.playground.user_posts.job;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.batch.playground.user_posts.config.DataConfig;
import com.example.spring.batch.playground.user_posts.config.batch.ItemIOConfig;
import com.example.spring.batch.playground.user_posts.entity.Post;
import com.example.spring.batch.playground.user_posts.entity.User;
import com.example.spring.batch.playground.user_posts.job.JobCompletionNotificationListener;
import com.example.spring.batch.playground.user_posts.job.UserItemProcessor;
import com.example.spring.batch.playground.user_posts.repository.UserRepository;
import com.google.common.collect.Lists;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Slf4j
class UserPostsBatchTest {

  @TestConfiguration
  static class Config {

    @Configuration
    static class TestDataConfig extends DataConfig {

    }

    @Configuration
    static class TestItemIOConfig extends ItemIOConfig {

    }

    @Configuration
    @EnableBatchProcessing
    static class BatchConfig {

      @Autowired
      private JobBuilderFactory jobBuilderFactory;

      @Autowired
      private StepBuilderFactory stepBuilderFactory;

      @Bean
      public Supplier<Job> importUserJobSupplier(
          JobCompletionNotificationListener listener,
          @Qualifier("userStep") Step userStep,
          @Qualifier("postStep") Step postStep) {
        return () -> jobBuilderFactory.get("importPersonJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(userStep)
            .next(postStep)
            .end()
            .build();
      }

      @Bean
      public Step userStep(
          ItemReader<User> reader,
          ItemProcessor<User, User> processor,
          ItemWriter<User> writer) {
        return stepBuilderFactory.get("userStep")
            .<User, User>chunk(10)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
      }

      @Bean
      public Step postStep(
          ItemReader<Post> reader,
          ItemProcessor<Post, Post> processor,
          ItemWriter<Post> writer) {
        return stepBuilderFactory.get("postStep")
            .<Post, Post>chunk(10)
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
  Supplier<Job> importUserJobSupplier;
  @Autowired
  JobLauncher jobLauncher;

  @Test
  void test() {
    var users = Lists.newArrayList(userRepository.findAll());
    log.info("{}", users);
    assertThat(users).hasSize(2);

    log.info("{}", importUserJobSupplier);
    var jobParameters = new JobParameters();
    try {
      var execution = jobLauncher.run(importUserJobSupplier.get(), jobParameters);
      assertThat(execution.getStatus())
          .isEqualTo(BatchStatus.COMPLETED);

      var finalUsers = Lists.newArrayList(userRepository.findAll());
      log.info("{}", finalUsers);
      assertThat(finalUsers).hasSize(7);
    } catch (JobExecutionAlreadyRunningException | JobRestartException |
             JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
      throw new RuntimeException(e);
    }
  }
}
