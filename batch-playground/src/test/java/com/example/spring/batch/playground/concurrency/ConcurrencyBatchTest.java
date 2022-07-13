package com.example.spring.batch.playground.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.batch.playground.user_posts.entity.User;
import com.example.spring.batch.playground.user_posts.job.JobCompletionNotificationListener;
import com.example.spring.batch.playground.user_posts.repository.UserRepository;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
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
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootTest
@Slf4j
class ConcurrencyBatchTest {

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
          var id = uniqueId;
          return jobBuilderFactory.get(id)
              .incrementer(new RunIdIncrementer())
              .listener(listener)
              .flow(userStep)
              .end()
              .build();
        };
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

          logData(stepExecution, "writer-before");

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

          logData(stepExecution, "writer-after");
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

      @Bean
      StepExecutionListener loggingStepExecutionListener() {
        return new StepExecutionListenerSupport() {
          @Override
          public void beforeStep(StepExecution stepExecution) {
            logData(stepExecution, "listener-beforeStep");
          }

          @Override
          public ExitStatus afterStep(StepExecution stepExecution) {
            logData(stepExecution, "listener-afterStep");
            return stepExecution.getExitStatus();
          }
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
            .listener(loggingStepExecutionListener())
            .build();
      }
    }
  }

  static void logData(StepExecution stepExecution, String tag) {
    log.info("Step :: Context :: {} | {} | {}",
        tag,
        stepExecution.getExecutionContext(),
        stepExecution);
    var jobExecution = stepExecution.getJobExecution();
    log.info("Job  :: Context :: {} | {} | {}",
        tag,
        jobExecution.getExecutionContext(),
        jobExecution);
  }

  @Autowired
  UserRepository userRepository;
  @Autowired
  Supplier<Job> importUserJobSupplier;
  @Autowired
  JobLauncher jobLauncher;
  Scheduler jobWorkerPool = Schedulers.newBoundedElastic(5, 100, "job");
  //  Scheduler jobWorkerPool = Schedulers.newBoundedElastic(1, 100, "job");
  Random random = new Random();

  @Test
  void test() {
    var users = Lists.newArrayList(userRepository.findAll());
    log.info("{}", users);
    assertThat(users).hasSize(0);

    log.info("{}", importUserJobSupplier);
    Flux.range(1, 10)
        .map(Object::toString)
//        .flatMap(this::runTestJob)
        .flatMap(this::runJob)
        .blockLast();

    log.info("Jobs completed");
  }

  private Mono<String> runJob(String jobId) {
    return newJob(jobId, randomSmallDelay(jobId))
        .doOnNext(id -> {
          log.info("Processing job: {}", id);
          try {
            var uniqueJobParameters = new JobParameters(Map.of("ID", new JobParameter(jobId)));
            var nonUniqueJobParameters = new JobParameters();
            var jobParameters = nonUniqueJobParameters;
            var execution = jobLauncher.run(importUserJobSupplier.get(), jobParameters);
            assertThat(execution.getStatus())
                .isEqualTo(BatchStatus.COMPLETED);
          } catch (JobExecutionAlreadyRunningException | JobRestartException |
                   JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private Mono<String> runTestJob(String jobId) {
    return newJob(jobId, randomDelay(jobId))
        .doOnNext(id -> log.info("Processing job: {}", id));
  }

  private Mono<String> newJob(String jobId, int delay) {
    log.info("Creating job: {}", jobId);
    return Mono.just(jobId)
        .delayElement(Duration.ofMillis(delay), jobWorkerPool);
  }

  private int noDelay(String jobId) {
    return 0;
  }

  private int randomDelay(String jobId) {
    return 200 + random.nextInt(200);
  }

  private int randomSmallDelay(String jobId) {
    return 50 + random.nextInt(100);
  }

  private int fixedDelay(String jobId) {
    return Integer.valueOf(jobId) * 200;
  }
}
