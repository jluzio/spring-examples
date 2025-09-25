package com.example.spring.batch.playground.features.user.config;

import com.example.spring.batch.playground.features.user.batch.PostFilterByUserItemProcessor;
import com.example.spring.batch.playground.features.user.persistence.model.Post;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

public class SharedJobConfig {

  @Bean
  public Step postImportExternalApiStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<Post> postExternalApiItemReader,
      PostFilterByUserItemProcessor postFilterByUserItemProcessor,
      ItemWriter<Post> postRepositoryItemWriter) {
    return new StepBuilder("postImportExternalApiStep", jobRepository)
        .<Post, Post>chunk(10, transactionManager)
        .reader(postExternalApiItemReader)
        .processor(postFilterByUserItemProcessor)
        .writer(postRepositoryItemWriter)
        .build();
  }

}
