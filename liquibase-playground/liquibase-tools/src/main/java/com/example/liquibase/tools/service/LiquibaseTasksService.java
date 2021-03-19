package com.example.liquibase.tools.service;

import static java.util.Optional.ofNullable;

import com.example.liquibase.tools.config.LiquibaseTasksProperties;
import com.example.liquibase.tools.model.Task;
import com.example.liquibase.tools.util.LoggerOutputStream;
import com.example.liquibase.tools.util.LoggerOutputStream.LogLevel;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@Slf4j
public class LiquibaseTasksService {

  private final LiquibaseFactory liquibaseFactory;
  private final LiquibaseTasksProperties properties;
  private final LiquibaseDataService liquibaseDataService;

  public void execute() {
    Flux.fromIterable(properties.getTasks())
        .doOnNext(task -> {
          try {
            runTask(task, properties);
          } catch (LiquibaseException e) {
            throw Exceptions.propagate(e);
          }
        })
        .blockLast();
  }

  private void runTask(Task task, LiquibaseTasksProperties globalProperties) throws LiquibaseException {
    log.info("{} :: running", task);

    try (PrintWriter outputWriter = new PrintWriter(new LoggerOutputStream(log, LogLevel.INFO))) {
      String changeLog = ofNullable(task.getChangeLog())
          .or(() -> ofNullable(globalProperties.getDefaultChangeLog()))
          .orElseThrow(NoSuchElementException::new);
      Liquibase liquibase = liquibaseFactory.get(changeLog);

      try {
        switch (task.getCommand()) {
          case VALIDATE:
            liquibase.validate();
            break;
          case STATUS:
            liquibase.reportStatus(
                task.isVerbose(), task.getContexts(), task.getLabels(),
                outputWriter);
            break;
          case FORCE_RELEASE_LOCKS:
            liquibase.forceReleaseLocks();
            break;
          case CLEAR_CHECKSUM:
            liquibase.clearCheckSums();
            break;
          case CLEAR_CHANGE_LOG:
            liquibaseDataService.clearChangeLog();
            break;
          case CHANGE_LOG_SYNC:
            liquibase.changeLogSync(
                task.getContexts(), task.getLabels());
            break;
          case CHANGE_LOG_SYNC_SQL:
            liquibase.changeLogSync(
                task.getContexts(), task.getLabels(), outputWriter);
            break;
          case UPDATE:
            liquibase.update(
                task.getContexts(), task.getLabels());
            break;
          case UPDATE_SQL:
            liquibase.update(
                task.getContexts(), task.getLabels(), outputWriter);
            break;
        }
      } catch (LiquibaseException e) {
        log.error("{} :: error", task, e);
        throw e;
      }
    }

    log.info("{} :: complete", task);
  }

}
