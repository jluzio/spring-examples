package com.example.spring.batch.playground.features.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotEmpty;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBatchTest
@SpringJUnitConfig(ProcessorTest.JobConfiguration.class)
@EnableAutoConfiguration
@Slf4j
class ProcessorTest {

  public record Item(String id, String value) {

  }

  public record IntermediateItem(@NotEmpty String id, @NotEmpty String value) {

  }
  public record OutputItem(String id) {

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
    ItemProcessor<Item, IntermediateItem> intermediateItemItemProcessor() {
      return item -> new IntermediateItem(item.id(), item.value());
    }

    @Bean
    public BeanValidatingItemProcessor<IntermediateItem> beanValidatingItemProcessor() throws Exception {
      BeanValidatingItemProcessor<IntermediateItem> beanValidatingItemProcessor = new BeanValidatingItemProcessor<>();
      beanValidatingItemProcessor.setFilter(true);
      return beanValidatingItemProcessor;
    }

    @Bean
    public ItemProcessor<IntermediateItem, IntermediateItem> filterBadIdItemProcessor() {
      return item -> Objects.equals(item.id(), "0") ? null : item;
    }

    @Bean
    public ItemProcessor<IntermediateItem, OutputItem> outputItemProcessor() {
      return item -> new OutputItem(item.id());
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<Item> itemReader,
        ItemProcessor<Item, IntermediateItem> intermediateItemItemProcessor,
        BeanValidatingItemProcessor<IntermediateItem> beanValidatingItemProcessor,
        ItemProcessor<IntermediateItem, IntermediateItem> filterBadIdItemProcessor,
        ItemProcessor<IntermediateItem, OutputItem> outputItemProcessor,
        ItemWriter<OutputItem> itemWriter
    ) {
      var processor = new CompositeItemProcessor<Item, OutputItem>(
          intermediateItemItemProcessor,
          beanValidatingItemProcessor,
          filterBadIdItemProcessor,
          outputItemProcessor
      );
      return new StepBuilder("step", jobRepository)
          .<Item, OutputItem>chunk(10, transactionManager)
          .reader(itemReader)
          .processor(processor)
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
  ItemWriter<OutputItem> itemWriter;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  void testJob() throws Exception {
    ArgumentCaptor<Chunk<OutputItem>> writeArgCaptor = ArgumentCaptor.captor();

    var items = List.of(
      new Item("0", "value0"),
      new Item("1", "value1"),
      new Item("2", "value2"),
      new Item("3", "")
    );
    var itemsAsString = objectMapper.writeValueAsString(items);
    var jobParameters = new JobParameters(Map.of(
        "data", new JobParameter<>(itemsAsString, String.class)
    ));

    var expectedItems = List.of(
        new OutputItem("1"),
        new OutputItem("2")
    );

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    verify(itemWriter)
        .write(writeArgCaptor.capture());
    assertThat(writeArgCaptor.getValue().getItems())
        .isEqualTo(expectedItems);
  }
}
