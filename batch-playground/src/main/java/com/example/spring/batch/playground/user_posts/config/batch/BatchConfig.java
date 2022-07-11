package com.example.spring.batch.playground.user_posts.config.batch;

import com.example.spring.batch.playground.user_posts.entity.Post;
import com.example.spring.batch.playground.user_posts.entity.User;
import com.example.spring.batch.playground.user_posts.job.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableBatchProcessing
@ConditionalOnProperty("app.batch.auto.enabled")
public class BatchConfig {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job importUserJob(
      JobCompletionNotificationListener listener,
      @Qualifier("userStep") Step userStep,
      @Qualifier("postStep") Step postStep) {
    return jobBuilderFactory.get("importPersonJob")
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
