package com.example.spring.batch.playground.features.core;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.CompositeItemReader;
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
@SpringJUnitConfig(ItemInterfacesTest.JobConfiguration.class)
@EnableAutoConfiguration
@Slf4j
class ItemInterfacesTest {

  static class JobConfiguration {

    /**
     * Current implementation is not thread-safe (would require a ThreadLocal)
     */
    @RequiredArgsConstructor
    static class JobParameterItemReader implements ItemReader<String>, ItemStream, StepExecutionListener {

      public static final String CURRENT_INDEX = "CURRENT_INDEX";
      private List<String> items;
      private int currentIndex;

      // restartable example implementation of open + update
      @Override
      public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey(CURRENT_INDEX)) {
          currentIndex = (int) executionContext.getLong(CURRENT_INDEX);
        } else {
          currentIndex = 0;
        }
      }

      // restartable example implementation of open + update
      public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putLong(CURRENT_INDEX, currentIndex);
      }

      @Override
      public void beforeStep(StepExecution stepExecution) {
        log.info("StepExecutionListener.beforeStep()");
        var itemsCsv = requireNonNull(stepExecution.getJobParameters().getString("data"));
        this.items = List.of(itemsCsv.split(","));
      }

      @Override
      public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (currentIndex < items.size()) {
          return items.get(currentIndex++);
        }

        return null;
      }
    }

    static class ListItemReaderWithListener<T> extends ListItemReader<T> implements ItemStreamReader<T> {

      private boolean open = false;

      public ListItemReaderWithListener(List<T> list) {
        super(list);
      }

      @Override
      public void open(ExecutionContext executionContext) throws ItemStreamException {
        open = true;
      }

      @Override
      public T read() {
        if (!open) {
          throw new IllegalStateException("Not opened!");
        }
        return super.read();
      }
    }

    @Bean
    public ItemReader<String> itemReader() {
      return new JobParameterItemReader();
    }

    /**
     * <b>NOTE</b>: when using any bean that is proxied by Spring, it must have the specific return type so that the
     * proxy is recognized to implement the listeners.
     * <h5>Options:</h5>
     * <ul>
     *   <li>return an interface that extends both ItemReader and the listener (e.g. ItemStreamReader)</li>
     *   <li>return the concrete type (ListItemReaderWithListener)</li>
     *   <li>configure as reader and also listener in the step</li>
     * </ul>
     */
    @Bean
    @StepScope
    public ItemStreamReader<String> itemReaderStepScope(
        @Value("#{jobParameters['data'].split(',')}") List<String> values) {
      return new ListItemReaderWithListener<>(values);
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
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<String> itemReaderStepScope,
        ItemWriter<String> itemWriter
    ) {
      return new StepBuilder("step2", jobRepository)
          .<String, String>chunk(10, transactionManager)
          .reader(itemReaderStepScope)
          .writer(itemWriter)
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

  @Test
  void testJob() throws Exception {
    ArgumentCaptor<Chunk<String>> writeArgCaptor = ArgumentCaptor.captor();

    var itemsAsString = "1,2";
    var items = Arrays.asList(itemsAsString.split(","));
    var jobParameters = new JobParameters(Map.of(
        "data", new JobParameter<>(itemsAsString, String.class)
    ));

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    verify(itemWriter, times(2))
        .write(writeArgCaptor.capture());
    assertThat(writeArgCaptor.getAllValues())
        .allSatisfy(it -> assertThat(it.getItems())
            .isEqualTo(items));
  }
}
