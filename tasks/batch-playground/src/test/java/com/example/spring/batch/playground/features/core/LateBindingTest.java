package com.example.spring.batch.playground.features.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.PassThroughItemProcessor;
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

    private final List<Class<?>> createdItemBeans = new ArrayList<>();

    @Bean
    List<Class<?>> createdItemBeans() {
      return createdItemBeans;
    }

    @Bean
    @StepScope
    public ItemReader<String> itemReader(@Value("#{jobParameters['data'].split(',')}") List<String> values) {
      createdItemBeans.add(ItemReader.class);
      return new ListItemReader<>(values);
    }

    @Bean
    @JobScope
    public ItemProcessor<String, String> itemProcessor() {
      createdItemBeans.add(ItemProcessor.class);
      return new PassThroughItemProcessor<>();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<String> itemReader,
        ItemProcessor<String,String> itemProcessor,
        ItemWriter<String> itemWriter
    ) {
      return new StepBuilder("step", jobRepository)
          .<String, String>chunk(10, transactionManager)
          .reader(itemReader)
          .processor(itemProcessor)
          .writer(itemWriter)
          .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<String> itemReader,
        ItemProcessor<String,String> itemProcessor
    ) {
      return new StepBuilder("step2", jobRepository)
          .<String, String>chunk(10, transactionManager)
          .reader(itemReader)
          .processor(itemProcessor)
          .writer(chunk -> {})
          .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step, Step step2) {
      return new JobBuilder("job", jobRepository)
          .start(step)
          .next(step2)
          .build();
    }
  }

  @Autowired
  JobLauncherTestUtils jobLauncherTestUtils;
  @MockitoBean
  ItemWriter<String> itemWriter;
  @Autowired
  List<Class<?>> createdItemBeans;

  @Test
  void testJob() throws Exception {
    ArgumentCaptor<Chunk<String>> writeArgCaptor = ArgumentCaptor.captor();

    // first task
    var itemsAsString = "1,2,3,4,5";
    var items = Arrays.asList(itemsAsString.split(","));
    var jobParameters = new JobParameters(Map.of(
        "data", new JobParameter<>(itemsAsString, String.class)
    ));

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    verify(itemWriter)
        .write(writeArgCaptor.capture());
    assertThat(writeArgCaptor.getValue().getItems())
        .isEqualTo(items);
    // StepScope: 1 per Step
    assertThat(createdItemBeans.stream().filter(ItemReader.class::equals).count())
        .isEqualTo(2);
    // JobScope: 1 per Job
    assertThat(createdItemBeans.stream().filter(ItemProcessor.class::equals).count())
        .isEqualTo(1);

    // second task
    var itemsAsStringTask2 = "1,2";
    var itemsTask2 = Arrays.asList(itemsAsStringTask2.split(","));
    var jobParametersTask2 = new JobParameters(Map.of(
        "data", new JobParameter<>(itemsAsStringTask2, String.class)
    ));

    var jobExecutionTask2 = jobLauncherTestUtils.launchJob(jobParametersTask2);

    assertEquals(ExitStatus.COMPLETED, jobExecutionTask2.getExitStatus());
    verify(itemWriter, times(2))
        .write(writeArgCaptor.capture());
    assertThat(writeArgCaptor.getValue().getItems())
        .isEqualTo(itemsTask2);
    // StepScope: 1 per Step (x2 tasks)
    assertThat(createdItemBeans.stream().filter(ItemReader.class::equals).count())
        .isEqualTo(4);
    // JobScope: 1 per Job (x2 tasks)
    assertThat(createdItemBeans.stream().filter(ItemProcessor.class::equals).count())
        .isEqualTo(2);
  }
}
