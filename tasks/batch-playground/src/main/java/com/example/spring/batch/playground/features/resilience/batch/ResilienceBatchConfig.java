package com.example.spring.batch.playground.features.resilience.batch;

import com.example.spring.batch.playground.features.resilience.persistence.service.JobDataService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnProperty(ResilienceBatchConfig.ENABLED_KEY)
@Slf4j
public class ResilienceBatchConfig {

  public static final String ENABLED_KEY = "app.features.resilience.enabled";

  @Bean
  Job resilientJob(
      JobRepository jobRepository,
      Step resilientStepA
  ) {
    return new JobBuilder("resilientJob", jobRepository)
        .start(resilientStepA)
        .build();
  }

  @Bean
  Step resilientStepA(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<String> resilientItemReader,
      ItemProcessor<String, String> resilientItemProcessor,
      BackOffPolicy resilienceBackOffPolicy
  ) {
    return new StepBuilder("resilientStepA", jobRepository)
        .<String, String>chunk(10, transactionManager)
        .reader(resilientItemReader)
        .processor(resilientItemProcessor)
        .writer(chunk -> log.info("processed items: {}", chunk.getItems()))
        .faultTolerant()
        .retry(IllegalStateException.class)
        .retryLimit(3)
        .backOffPolicy(resilienceBackOffPolicy)
        // so it doesn't call processor on previous items again on retry
        .processorNonTransactional()
        .build();
  }

  @Bean
  @StepScope
  public BackOffPolicy resilienceBackOffPolicy(@Value("#{jobParameters['backOffPeriod']}") Long backOffPeriod) {
    var resolvedBackOffPeriod = Optional.ofNullable(backOffPeriod)
        .orElse(TimeUnit.SECONDS.toMillis(2));

    FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(resolvedBackOffPeriod);
    return backOffPolicy;
  }

  @Bean
  @StepScope
  public ItemReader<String> resilientItemReader(@Value("#{jobParameters['data'].split(',')}") List<String> values) {
    return new ListItemReader<>(values);
  }

  @Bean
  @StepScope
  public FaultyItemProcessor resilientItemProcessor(
      JobDataService jobDataService,
      @Value("#{jobParameters['failuresMap']}") String failuresMapParam) {
    return new FaultyItemProcessor(jobDataService, failuresMapParam);
  }

  public static class FaultyItemProcessor implements ItemProcessor<String, String>, StepExecutionListener {

    private final JobDataService jobDataService;
    private final Map<String, AtomicInteger> failuresMap;
    private StepExecution stepExecution;

    public FaultyItemProcessor(JobDataService jobDataService, String failuresMapParam) {
      this.jobDataService = jobDataService;
      this.failuresMap = Arrays.stream(failuresMapParam.split(","))
          .map(v -> {
            var tokens = v.split("=");
            return Map.entry(tokens[0], new AtomicInteger(Integer.parseInt(tokens[1])));
          })
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
      this.stepExecution = stepExecution;
    }

    @Override
    public String process(String item) throws Exception {
      var retryCount = Objects.requireNonNull(RetrySynchronizationManager.getContext()).getRetryCount();
      var failuresForItem = failuresMap.get(item);
      log.debug(
          "{} :: item={} :: retryCount={} | failuresForItem={} | failuresMap={} | stepExecution={} | executionContext={} | jobExecutionContext={}",
          getClass().getSimpleName(), item, retryCount, failuresForItem, failuresMap, stepExecution,
          stepExecution.getExecutionContext(), stepExecution.getJobExecution().getExecutionContext());

      jobDataService.updateJobData(
          stepExecution.getJobExecution().getJobId().toString(),
          "item: %s | retryCount=%s".formatted(item, retryCount)
      );

      if (failuresForItem.get() > 0) {
        log.info("{}} :: item={} :: exception",
            getClass().getSimpleName(), item);
        failuresForItem.decrementAndGet();
        throw new IllegalStateException(item);
      }
      log.info("{} :: item={} :: completed",
          getClass().getSimpleName(), item);
      return item;
    }
  }

}
