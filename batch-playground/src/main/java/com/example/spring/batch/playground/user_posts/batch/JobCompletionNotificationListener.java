package com.example.spring.batch.playground.user_posts.batch;

import com.example.spring.batch.playground.user_posts.persistence.repository.PostRepository;
import com.example.spring.batch.playground.user_posts.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {

  private final UserRepository userRepository;
  private final PostRepository postRepository;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.info("### Job starting ###");
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info("### Job finished with status {} ###", jobExecution.getStatus());
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      userRepository.findAll()
          .forEach(user -> log.info("Found user <{}> in the database.", user));
      postRepository.findAll()
          .forEach(post -> log.info("Found post <{}> in the database.", post));
    }
  }
}