package com.example.spring.batch.playground.features.user.config;

import com.example.spring.batch.playground.features.user.batch.JobCompletionNotificationListener;
import com.example.spring.batch.playground.features.user.batch.UserEnsureIdItemProcessor;
import com.example.spring.batch.playground.features.user.persistence.model.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

public class ImportDataJobConfig {

  @Bean
  public Job userImportDataJob(
      JobRepository jobRepository,
      JobCompletionNotificationListener listener,
      Step userImportDataStep,
      Step postImportExternalApiStep
  ) {
    return new JobBuilder("userImportDataJob", jobRepository)
        .listener(listener)
        .start(userImportDataStep)
        .next(postImportExternalApiStep)
        .build();
  }

  @Bean
  public Step userImportDataStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<User> userParameterDataItemReader,
      UserEnsureIdItemProcessor userEnsureIdItemProcessor,
      ItemWriter<User> userRepositoryItemWriter
  ) {
    return new StepBuilder("userImportDataStep", jobRepository)
        .<User, User>chunk(10, transactionManager)
        .reader(userParameterDataItemReader)
        .processor(userEnsureIdItemProcessor)
        .writer(userRepositoryItemWriter)
        .build();
  }

}
