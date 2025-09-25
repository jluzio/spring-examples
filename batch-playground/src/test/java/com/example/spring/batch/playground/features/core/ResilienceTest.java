package com.example.spring.batch.playground.features.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.batch.playground.features.core.item.JobParameterCsvItemReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBatchTest
@SpringJUnitConfig(ResilienceTest.JobConfiguration.class)
@EnableAutoConfiguration
@Slf4j
class ResilienceTest {

  static class JobConfiguration {

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<String> itemReader,
        ItemProcessor<String, String> itemProcessor,
        ItemWriter<String> itemWriter
    ) {
      var backOffPolicy = new FixedBackOffPolicy();
      backOffPolicy.setBackOffPeriod(100);

      return new StepBuilder("step", jobRepository)
          .<String, String>chunk(1, transactionManager)
          .reader(itemReader)
          .processor(itemProcessor)
          .writer(itemWriter)
          .faultTolerant()
          .retryLimit(3)
          .retry(IOException.class)
          .backOffPolicy(backOffPolicy)
          .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
      return new JobBuilder("job", jobRepository)
          .start(step)
          .build();
    }

    @Bean
    ItemReader<String> itemReader() {
      return new JobParameterCsvItemReader("scenario");
    }

    @Bean
    CompositeItemProcessor<String, String> itemProcessor() {
      return new CompositeItemProcessor<>(List.of(new PassThroughItemProcessor<String>()));
    }
  }

  @Autowired
  JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired
  CompositeItemProcessor<String, String> itemProcessor;
  @MockitoBean
  ItemWriter<String> itemWriter;

  @Test
  void testJob_retriable_fail_then_success() throws Exception {
    AtomicInteger callCounter = new AtomicInteger(0);
    var delegateProcessor = processor(callCounter, count -> count % 3 == 0, () -> new IOException("Some IOException"));
    mockItemProcessor(delegateProcessor);

    var jobParameters = getUniqueJobParameters("testJob_retriable_fail_then_success");
    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    log.info("jobExecution: {}", jobExecution);

    assertThat(jobExecution.getExitStatus())
        .isEqualTo(ExitStatus.COMPLETED);
    assertThat(callCounter.get())
        .isEqualTo(3);
  }

  @Test
  void testJob_retriable_fail() throws Exception {
    AtomicInteger callCounter = new AtomicInteger(0);
    var delegateProcessor = processor(callCounter, _ -> false, () -> new IOException("Some IOException"));
    mockItemProcessor(delegateProcessor);

    var jobParameters = getUniqueJobParameters("testJob_retriable_fail");
    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    log.info("jobExecution: {}", jobExecution);

    assertThat(jobExecution.getExitStatus().getExitCode())
        .isEqualTo(ExitStatus.FAILED.getExitCode());
    assertThat(jobExecution.getExitStatus().getExitDescription())
        .contains(IOException.class.getName());
    assertThat(callCounter.get())
        .isEqualTo(3);
  }

  @Test
  void testJob_non_retriable_fail() throws Exception {
    AtomicInteger callCounter = new AtomicInteger(0);
    var delegateProcessor = processor(callCounter, _ -> false,
        () -> new IllegalArgumentException("Some IllegalArgumentException"));
    mockItemProcessor(delegateProcessor);

    var jobParameters = getUniqueJobParameters("testJob_non_retriable_fail");
    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    log.info("jobExecution: {}", jobExecution);

    assertThat(jobExecution.getExitStatus().getExitCode())
        .isEqualTo(ExitStatus.FAILED.getExitCode());
    assertThat(jobExecution.getExitStatus().getExitDescription())
        .contains(IllegalArgumentException.class.getName());
    assertThat(callCounter.get())
        .isEqualTo(1);
  }

  @Test
  void testJob_success() throws Exception {
    AtomicInteger callCounter = new AtomicInteger(0);
    var delegateProcessor = processor(callCounter, _ -> true, null);
    mockItemProcessor(delegateProcessor);

    var jobParameters = getUniqueJobParameters("testJob_success");
    var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    log.info("jobExecution: {}", jobExecution);

    assertThat(jobExecution.getExitStatus())
        .isEqualTo(ExitStatus.COMPLETED);
    assertThat(callCounter.get())
        .isEqualTo(1);
  }

  private void mockItemProcessor(ItemProcessor<String, String> delegateProcessor) throws Exception {
    itemProcessor.setDelegates(List.of(delegateProcessor));
    itemProcessor.afterPropertiesSet();
  }

  private JobParameters getUniqueJobParameters(String scenario) {
    var parameters = new HashMap<>(jobLauncherTestUtils.getUniqueJobParameters().getParameters());
    parameters.put("scenario", new JobParameter<>(scenario, String.class));
    return new JobParameters(parameters);
  }

  private ItemProcessor<String, String> processor(AtomicInteger callCounter, Function<Integer, Boolean> successFunc,
      Supplier<Exception> exceptionSupplier) {
    return item -> {
      int callNumber = callCounter.incrementAndGet();
      var success = successFunc.apply(callNumber);
      log.info("processor :: call={} | success={} | item={} | exceptionSupplier={}",
          callCounter,
          success,
          item,
          exceptionSupplier);

      if (success) {
        return item;
      }
      throw exceptionSupplier.get();
    };
  }
}
