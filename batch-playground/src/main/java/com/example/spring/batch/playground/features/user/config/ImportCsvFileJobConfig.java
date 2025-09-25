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

public class ImportCsvFileJobConfig {

  @Bean
  public Job userImportCsvFileJob(
      JobRepository jobRepository,
      JobCompletionNotificationListener listener,
      Step userImportCsvFileStep,
      Step postImportExternalApiStep
  ) {
    return new JobBuilder("userImportCsvFileJob", jobRepository)
        .listener(listener)
        .start(userImportCsvFileStep)
        .next(postImportExternalApiStep)
        .build();
  }

  @Bean
  public Step userImportCsvFileStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<User> userSampleDataCsvItemReader,
      UserEnsureIdItemProcessor userEnsureIdItemProcessor,
      ItemWriter<User> userRepositoryItemWriter
  ) {
    return new StepBuilder("userImportCsvFileStep", jobRepository)
        .<User, User>chunk(10, transactionManager)
        .reader(userSampleDataCsvItemReader)
        .processor(userEnsureIdItemProcessor)
        .writer(userRepositoryItemWriter)
        .build();
  }

}
