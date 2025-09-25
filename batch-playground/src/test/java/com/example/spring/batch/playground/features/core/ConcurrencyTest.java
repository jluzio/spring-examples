package com.example.spring.batch.playground.features.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.batch.playground.features.core.item.DelayingItemProcessor;
import com.example.spring.batch.playground.features.core.item.JobParameterCsvItemReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Future.State;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
@Slf4j
class ConcurrencyTest {

  @TestConfiguration
  static class JobConfiguration {

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        ItemReader<String> itemReader,
        ItemProcessor<String, String> itemProcessor,
        ItemWriter<String> itemWriter
    ) {
      return new StepBuilder("step", jobRepository)
          .<String, String>chunk(1, transactionManager)
          .reader(itemReader)
          .processor(itemProcessor)
          .writer(itemWriter)
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
      return new JobParameterCsvItemReader("data");
    }

    @Bean
    ItemProcessor<String, String> itemProcessor() {
      return new DelayingItemProcessor<>(Duration.ofMillis(100), new PassThroughItemProcessor<>());
    }
  }

  @Autowired
  Job job;
  @Autowired
  JobLauncher jobLauncher;
  @MockitoSpyBean
  ItemProcessor<String, String> itemProcessor;
  @MockitoBean
  ItemWriter<String> itemWriter;

  @Test
  void testJob() throws Exception {
    List<String> items = IntStream.rangeClosed(1, 5)
        .mapToObj(Objects::toString)
        .toList();
    List<Callable<JobExecution>> tasks = IntStream.rangeClosed(1, 10)
        .mapToObj(Objects::toString)
        .map(taskId -> task(taskId, items))
        .toList();

    try (var executorService = Executors.newFixedThreadPool(5)) {
      List<Future<JobExecution>> futures = executorService.invokeAll(tasks);

      log.info("===Tasks===");
      IntStream.rangeClosed(1, futures.size())
          .forEach(runId -> {
            Future<JobExecution> f = futures.get(runId - 1);
            log.info("run[{}] :: state={} | {}", runId, f.state(), f);
          });
      log.info("---Tasks---");

      for (var future : futures) {
        assertThat(future.isDone())
            .isTrue();
        assertThat(future.state())
            .isEqualTo(State.SUCCESS);
        assertThat(future.get().getExitStatus().getExitCode())
            .isEqualTo(ExitStatus.COMPLETED.getExitCode());
      }

      for (String item : items) {
        verify(itemProcessor, times(tasks.size())).process(item);
      }
    }
  }

  private Callable<JobExecution> task(String taskId, List<String> items) {
    return () -> {
      var jobParameters = getUniqueJobParameters(taskId, items);
      var jobExecution = jobLauncher.run(job, jobParameters);
      log.info("jobExecution: {}", jobExecution);
      return jobExecution;
    };
  }

  private JobParameters getUniqueJobParameters(String runId, List<String> items) {
    var parameters = new HashMap<>(new JobParameters().getParameters());
    parameters.put("taskId", new JobParameter<>(runId, String.class));
    parameters.put("data", new JobParameter<>(String.join(",", items), String.class));
    return new JobParameters(parameters);
  }

}
