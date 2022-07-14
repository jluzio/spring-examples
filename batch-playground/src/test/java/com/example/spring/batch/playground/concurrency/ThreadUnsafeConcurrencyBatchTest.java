package com.example.spring.batch.playground.concurrency;

import static com.example.spring.batch.playground.util.LogExecutionContextHelper.logData;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.batch.playground.listener.LogExecutionContextJobExecutionListener;
import com.example.spring.batch.playground.listener.LogExecutionContextStepExecutionListener;
import com.example.spring.batch.playground.user_posts.entity.User;
import com.example.spring.batch.playground.user_posts.job.JobCompletionNotificationListener;
import com.example.spring.batch.playground.user_posts.repository.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@Slf4j
class ThreadUnsafeConcurrencyBatchTest {

  public static final String USER_IDS = "USER_IDS";

  @TestConfiguration
  static class Config {

    @Configuration
    @EnableBatchProcessing
    static class BatchConfig {

      @Autowired
      private JobBuilderFactory jobBuilderFactory;
      @Autowired
      private StepBuilderFactory stepBuilderFactory;

      @Bean
      public Supplier<Job> importUserJobSupplier(
          JobCompletionNotificationListener listener,
          @Qualifier("userStep") Step userStep) {
        return () -> {
          var nonUniqueId = "importPersonJob";
          var uniqueId = "importPersonJob-" + UUID.randomUUID();
          var id = nonUniqueId;
          return jobBuilderFactory.get(id)
              .incrementer(new RunIdIncrementer())
              .listener(listener)
              .listener(new LogExecutionContextJobExecutionListener())
              .flow(userStep)
              .end()
              .build();
        };
      }


      @Bean
      public Step userStep(
          ItemReader<User> reader,
          ItemWriter<User> writer) {
        return stepBuilderFactory.get("userStep")
            .<User, User>chunk(1)
            .reader(reader)
            .processor(new PassThroughItemProcessor<>())
            .writer(writer)
            .listener(new LogExecutionContextStepExecutionListener(true))
            .build();
      }
      @Bean
      public ItemReader<User> userItemReader(
          @Qualifier("userDataResource") Resource userDataResource) {
        return new FlatFileItemReaderBuilder<User>()
            .name("userItemReader")
            .resource(userDataResource)
            .delimited()
            .names("name", "username", "email")
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
              setTargetType(User.class);
            }})
            .build();
      }

      @Bean
      Resource userDataResource() throws IOException {
        var resourcePath = new ClassPathResource("sample-data.csv").getFile().toPath();
        return new ByteArrayResource(Files.readAllBytes(resourcePath));
      }

      @Component
      static class UserRepositoryItemWriter implements ItemWriter<User>, StepExecutionListener {

        private final ItemWriter<User> delegate;
        private StepExecution stepExecution;

        public UserRepositoryItemWriter(UserRepository repository) {
          delegate = new RepositoryItemWriterBuilder<User>()
              .repository(repository)
              .methodName("save")
              .build();
        }

        @Override
        public void write(List<? extends User> items) throws Exception {
          delegate.write(items);

          logDataExecutions(stepExecution, "writer-before");

          var stepExecutionCtx = stepExecution.getExecutionContext();
          if (!stepExecutionCtx.containsKey(USER_IDS)) {
            stepExecutionCtx.put(USER_IDS, new ArrayList<>());
          }
          @SuppressWarnings("unchecked")
          List<Long> userIds = (List<Long>) stepExecutionCtx.get(USER_IDS);
          userIds.addAll(
              items.stream().map(User::getId).toList());

          var jobExecutionCtx = stepExecution.getJobExecution().getExecutionContext();
          jobExecutionCtx.put(USER_IDS, userIds);

          logDataExecutions(stepExecution, "writer-after");
        }

        @Override
        public void beforeStep(StepExecution stepExecution) {
          this.stepExecution = stepExecution;
        }

        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
          return stepExecution.getExitStatus();
        }
      }
    }
  }

  static void logDataExecutions(StepExecution stepExecution, String tag) {
    logData(stepExecution, stepExecution.getExecutionContext(), tag);
    var jobExecution = stepExecution.getJobExecution();
    logData(jobExecution, jobExecution.getExecutionContext(), tag);
  }

  @Autowired
  UserRepository userRepository;
  @Autowired
  Supplier<Job> importUserJobSupplier;
  @Autowired
  JobLauncher jobLauncher;

  @Test
  void test() {
    var jobRunner = new BatchJobRunner(5, BatchJobRunner::randomSmallDelay);

    log.info("{}", importUserJobSupplier);
    Flux.range(1, 10)
        .map(Object::toString)
        .flatMap(jobId -> runJob(jobId, jobRunner))
        .blockLast();

    log.info("Jobs completed");
  }

  private Mono<String> runJob(String jobId, BatchJobRunner jobRunner) {
    log.info("Creating job: {}", jobId);
    return jobRunner.job(jobId)
        .doOnNext(id -> {
          log.info("Processing job: {}", id);
          try {
            var uniqueJobParameters = new JobParameters(Map.of("ID", new JobParameter(jobId)));
            var nonUniqueJobParameters = new JobParameters();
            var jobParameters = uniqueJobParameters;
            var execution = jobLauncher.run(importUserJobSupplier.get(), jobParameters);
            assertThat(execution.getStatus())
                .isEqualTo(BatchStatus.COMPLETED);
          } catch (JobExecutionAlreadyRunningException | JobRestartException |
                   JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private Mono<String> runTestJob(String jobId, BatchJobRunner jobRunner) {
    log.info("Creating job: {}", jobId);
    return jobRunner.job(jobId)
        .doOnNext(id -> log.info("Processing job: {}", id));
  }
}
