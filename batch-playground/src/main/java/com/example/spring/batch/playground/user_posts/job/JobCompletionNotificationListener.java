package com.example.spring.batch.playground.user_posts.job;

import com.example.spring.batch.playground.user_posts.repository.PostRepository;
import com.example.spring.batch.playground.user_posts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

  private final UserRepository userRepository;
  private final PostRepository postRepository;


  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED! Time to verify the results");

      Flux.fromIterable(userRepository.findAll())
          .doOnNext(user -> log.info("Found user <{}> in the database.", user))
          .blockLast();
      Flux.fromIterable(postRepository.findAll())
          .doOnNext(post -> log.info("Found post <{}> in the database.", post))
          .blockLast();
    }
  }
}