package com.example.spring.batch.playground.features.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
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
@SpringJUnitConfig(ContextDataPropagationWithSerializableItemTest.JobConfiguration.class)
@EnableAutoConfiguration
@Slf4j
class ContextDataPropagationWithSerializableItemTest {

  public static final String CURRENT_DATA_KEY = "CURRENT_DATA";

  public record Item(String id, String value) implements Serializable {

  }

  static class JobConfiguration {

    static class SavingItemWriter implements ItemWriter<Item> {

      private final List<Item> storedItems = new ArrayList<>();
      private StepExecution stepExecution;

      @BeforeStep
      public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
      }

      @Override
      public void write(Chunk<? extends Item> chunk) throws Exception {
        storedItems.addAll(chunk.getItems());
        stepExecution.getExecutionContext().put(CURRENT_DATA_KEY, storedItems);
      }
    }

    static class RestoringItemWriter implements ItemReader<Item> {

      private List<Item> items;
      private int currentIndex = 0;

      @BeforeStep
      @SuppressWarnings("unchecked")
      public void beforeStep(StepExecution stepExecution) {
        this.items = (List<Item>) stepExecution.getJobExecution().getExecutionContext().get(CURRENT_DATA_KEY);
        this.currentIndex = 0;
      }

      @Override
      public Item read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return currentIndex < items.size()
            ? items.get(currentIndex++)
            : null;
      }
    }

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
    public ExecutionContextPromotionListener promotionListener() {
      var listener = new ExecutionContextPromotionListener();
      listener.setKeys(new String[] {CURRENT_DATA_KEY});
      return listener;
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<Item> itemReader,
        ExecutionContextPromotionListener executionContextPromotionListener
    ) {
      return new StepBuilder("step1", jobRepository)
          .<Item, Item>chunk(10, transactionManager)
          .reader(itemReader)
          .writer(new SavingItemWriter())
          .listener(executionContextPromotionListener)
          .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemWriter<Item> itemWriter
    ) {
      return new StepBuilder("step2", jobRepository)
          .<Item, Item>chunk(10, transactionManager)
          .reader(new RestoringItemWriter())
          .writer(itemWriter)
          .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step1, Step step2) {
      return new JobBuilder("job", jobRepository)
          .start(step1)
          .next(step2)
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
