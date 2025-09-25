package com.example.spring.batch.playground.features.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBatchTest
@SpringJUnitConfig(LateBindingTest.JobConfiguration.class)
@EnableAutoConfiguration
@Slf4j
class LateBindingTest {

  static class JobConfiguration {

    @Bean
    @StepScope
    public ItemReader<String> itemReader(@Value("#{jobParameters['data'].split(',')}") List<String> values) {
      return new ListItemReader<>(values);
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<String> itemReader,
        ItemWriter<String> itemWriter
    ) {
      return new StepBuilder("step", jobRepository)
          .<String, String>chunk(10, transactionManager)
          .reader(itemReader)
          .writer(itemWriter)
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
  JobLauncherTestUtils jobLauncherTestUtils;
  @MockitoBean
  ItemWriter<String> itemWriter;

  @Test
  void testJob() throws Exception {
    var itemsAsString = "1,2,3,4,5";
    var items = Arrays.asList(itemsAsString.split(","));
    var jobParameters = new JobParameters(Map.of(
        "data", new JobParameter<>(itemsAsString, String.class)
    ));

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

    ArgumentCaptor<Chunk<String>> writeArgCaptor = ArgumentCaptor.captor();
    verify(itemWriter)
        .write(writeArgCaptor.capture());
    assertThat(writeArgCaptor.getValue().getItems())
        .isEqualTo(items);
  }
}
