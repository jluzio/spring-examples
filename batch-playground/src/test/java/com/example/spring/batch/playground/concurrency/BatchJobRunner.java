package com.example.spring.batch.playground.concurrency;

import java.time.Duration;
import java.util.Random;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class BatchJobRunner {

  private final Function<String, Integer> delaySupplier;
  private final Scheduler scheduler;
  private static final Random random = new Random();

  public BatchJobRunner(int threadCap, Function<String, Integer> delaySupplier) {
    this.delaySupplier = delaySupplier;
    this.scheduler = Schedulers.newBoundedElastic(threadCap, 100, "job");
  }

  public Mono<String> job(String jobId) {
    return Mono.just(jobId)
        .delayElement(Duration.ofMillis(delaySupplier.apply(jobId)), scheduler);
  }

  public static int noDelay(String jobId) {
    return 0;
  }

  public static int randomDelay(String jobId) {
    return 200 + random.nextInt(200);
  }

  public static int randomSmallDelay(String jobId) {
    return 50 + random.nextInt(100);
  }

  public static int fixedDelay(String jobId) {
    return Integer.valueOf(jobId) * 200;
  }

}
