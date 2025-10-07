package com.example.spring.batch.playground.features.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.example.spring.batch.playground.features.core.LoggingTest.JobConfiguration.AnnotationItemFailureLoggerListener;
import com.example.spring.batch.playground.features.core.LoggingTest.JobConfiguration.ItemFailureLoggerListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.core.annotation.OnWriteError;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBatchTest
@SpringJUnitConfig(LoggingTest.JobConfiguration.class)
@EnableAutoConfiguration
@Slf4j
class LoggingTest {

  static class JobConfiguration {

    @Slf4j
    static class ItemFailureLoggerListener extends ItemListenerSupport<Object, Object> {

      @Override
      public void onReadError(Exception ex) {
        log.error("Encountered error on read", ex);
      }

      @Override
      public void onProcessError(Object item, Exception ex) {
        log.error("Encountered error on process", ex);
      }

      @Override
      public void onWriteError(Exception ex, Chunk<?> items) {
        log.error("Encountered error on write", ex);
      }
    }

    @Slf4j
    static class AnnotationItemFailureLoggerListener {

      @OnReadError
      public void onReadError(Exception ex) {
        log.error("Encountered error on read", ex);
      }

      @OnProcessError
      public void onProcessError(Object item, Exception ex) {
        log.error("Encountered error on process", ex);
      }

      @OnWriteError
      public void onWriteError(Exception ex, Chunk<?> items) {
        log.error("Encountered error on write", ex);
      }
    }

    @Bean
    ItemFailureLoggerListener itemFailureLoggerListener() {
      var listener = new ItemFailureLoggerListener();
      return spy(listener);
    }

    @Bean
    AnnotationItemFailureLoggerListener annotationItemFailureLoggerListener() {
      var listener = new AnnotationItemFailureLoggerListener();
      return spy(listener);
    }

    @Bean
    ItemWriter<String> itemWriter() throws Exception {
      return _ -> {
        throw new IOException("Some exception");
      };
    }

    @Bean
    @StepScope
    public ItemReader<String> itemReader(@Value("#{jobParameters['data'].split(',')}") List<String> values) {
      return new ListItemReader<>(values);
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<String> itemReader,
        ItemWriter<String> itemWriter,
        ItemFailureLoggerListener itemFailureLoggerListener,
        AnnotationItemFailureLoggerListener annotationItemFailureLoggerListener
    ) {
      return new StepBuilder("step", jobRepository)
          .<String, String>chunk(10, transactionManager)
          .reader(itemReader)
          .writer(itemWriter)
          // casting with Type<Object> so the right method is called
          // otherwise org.springframework.batch.core.step.builder.SimpleStepBuilder.listener(java.lang.Object) is called
          .listener((ItemReadListener<Object>) itemFailureLoggerListener)
          .listener((ItemWriteListener<Object>) itemFailureLoggerListener)
          .listener((ItemProcessListener<Object, Object>) itemFailureLoggerListener)
          // using org.springframework.batch.core.step.builder.SimpleStepBuilder.listener(java.lang.Object)
          .listener(annotationItemFailureLoggerListener)
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
  @Autowired
  ItemFailureLoggerListener itemFailureLoggerListener;
  @Autowired
  AnnotationItemFailureLoggerListener annotationItemFailureLoggerListener;

  @Test
  void testJob() throws Exception {
    var itemsAsString = "1,2,3,4,5";
    var jobParameters = new JobParameters(Map.of(
        "data", new JobParameter<>(itemsAsString, String.class)
    ));

    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    assertThat(jobExecution.getExitStatus().getExitCode())
        .isEqualTo(ExitStatus.FAILED.getExitCode());
    verify(itemFailureLoggerListener)
        .onWriteError(any(IOException.class), any());
    verify(annotationItemFailureLoggerListener)
        .onWriteError(any(IOException.class), any());
  }
}
