package com.example.spring.batch.playground.user_posts.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.example.spring.batch.playground.user_posts.config.DataConfig;
import com.example.spring.batch.playground.user_posts.config.batch.ItemIOConfig;
import com.example.spring.batch.playground.user_posts.entity.Post;
import com.example.spring.batch.playground.user_posts.entity.User;
import com.example.spring.batch.playground.user_posts.job.JobCompletionNotificationListener;
import com.example.spring.batch.playground.user_posts.repository.ErrorHandlingBatchTest.Config.BatchConfig.ExecutionContextJobExecutionListener;
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
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
class ErrorHandlingBatchTest {

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
          ExecutionContextJobExecutionListener listener,
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
          ItemProcessor<User, User> processor,
          ItemWriter<User> writer) {
        ItemReader<User> reader = () -> {
          throw new UnexpectedInputException("Random read error");
        };
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

      @Component
      static class ExecutionContextJobExecutionListener extends JobExecutionListenerSupport {

        @Override
        public void beforeJob(JobExecution jobExecution) {
         jobExecution.getExecutionContext().put("dataKey1", "value1");
        }

        @Override
        public void afterJob(JobExecution jobExecution) {
          log.info("{}", jobExecution.getExecutionContext());
          log.info("clearing ExecutionContext");
          jobExecution.setExecutionContext(new ExecutionContext());
        }
      }
    }
  }

  @Autowired
  UserRepository userRepository;
  @Autowired
  Supplier<Job> importUserJobSupplier;
  @Autowired
  JobLauncher jobLauncher;
  @SpyBean
  ExecutionContextJobExecutionListener executionContextJobExecutionListener;

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
          .isEqualTo(BatchStatus.FAILED);

      var finalUsers = Lists.newArrayList(userRepository.findAll());
      log.info("{}", finalUsers);
      assertThat(finalUsers).hasSize(2);

      verify(executionContextJobExecutionListener).beforeJob(any());
      verify(executionContextJobExecutionListener).afterJob(any());
    } catch (JobExecutionAlreadyRunningException | JobRestartException |
             JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
      throw new RuntimeException(e);
    }
  }
}
