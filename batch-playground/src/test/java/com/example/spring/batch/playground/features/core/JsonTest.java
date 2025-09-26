package com.example.spring.batch.playground.features.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBatchTest
@SpringJUnitConfig(JsonTest.JobConfiguration.class)
@EnableAutoConfiguration
@Slf4j
class JsonTest {

  public record Item(String id, String value) {

  }

  static class JobConfiguration {

    @Bean
    @StepScope
    public JsonItemReader<Item> itemReader(@Value("#{jobParameters['data']}") String data) {
      return new JsonItemReaderBuilder<Item>()
          .name("jsonReader")
          .jsonObjectReader(new JacksonJsonObjectReader<>(Item.class))
          .resource(new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8)))
          .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<Item> itemReader,
        ItemWriter<Item> itemWriter
    ) {
      return new StepBuilder("step", jobRepository)
          .<Item, Item>chunk(10, transactionManager)
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
  ItemWriter<Item> itemWriter;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  void testJob() throws Exception {
    ArgumentCaptor<Chunk<Item>> writeArgCaptor = ArgumentCaptor.captor();

    var items = List.of(
      new Item("1", "value1"),
      new Item("2", "value2")
    );
    var itemsAsString = objectMapper.writeValueAsString(items);
    var jobParameters = new JobParameters(Map.of(
        "data", new JobParameter<>(itemsAsString, String.class)
    ));

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    verify(itemWriter)
        .write(writeArgCaptor.capture());
    assertThat(writeArgCaptor.getValue().getItems())
        .isEqualTo(items);
  }
}
