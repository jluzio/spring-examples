package com.example.spring.batch.playground.guide.listener;

import com.example.spring.batch.playground.guide.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

  private final PersonRepository personRepository;


  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED! Time to verify the results");

      Flux.fromIterable(personRepository.findAll())
          .doOnNext(person -> log.info("Found <" + person + "> in the database."))
          .blockLast();
    }
  }
}