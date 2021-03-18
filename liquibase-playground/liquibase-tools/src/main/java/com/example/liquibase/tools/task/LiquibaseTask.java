package com.example.liquibase.tools.task;

import com.example.liquibase.tools.config.LiquibaseTaskProperties;
import com.example.liquibase.tools.config.LiquibaseTaskProperties.Command;
import com.example.liquibase.tools.repository.DatabaseChangelogRepository;
import java.io.PrintWriter;
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
public class LiquibaseTask {

  private final Liquibase liquibase;
  private final LiquibaseTaskProperties properties;
  private final DatabaseChangelogRepository changelogRepository;

  void execute() {
    Flux.fromIterable(properties.getCommands())
        .doOnNext(command -> {
          try {
            runCommand(command);
          } catch (LiquibaseException e) {
            throw Exceptions.propagate(e);
          }
        })
        .blockLast();
  }

  private void runCommand(Command command) throws LiquibaseException {
    log.info("{} :: running", command);
    PrintWriter outputWriter = new PrintWriter(System.out);

    try {
      switch (command) {
        case VALIDATE:
          liquibase.validate();
          break;
        case STATUS:
          liquibase.reportStatus(
              properties.isVerbose(), properties.getContexts(), properties.getLabels(),
              outputWriter);
          break;
        case FORCE_RELEASE_LOCKS:
          liquibase.forceReleaseLocks();
          break;
        case CLEAR_CHECKSUM:
          liquibase.clearCheckSums();
          break;
        case CLEAR_CHANGE_LOG:
          changelogRepository.deleteAll();
          break;
        case CHANGE_LOG_SYNC:
          liquibase.changeLogSync(
              properties.getContexts(), properties.getLabels());
          break;
        case CHANGE_LOG_SYNC_SQL:
          liquibase.changeLogSync(
              properties.getContexts(), properties.getLabels(), outputWriter);
          break;
        case UPDATE:
          liquibase.update(
              properties.getContexts(), properties.getLabels());
          break;
        case UPDATE_SQL:
          liquibase.update(
              properties.getContexts(), properties.getLabels(), outputWriter);
          break;
      }
    } catch (LiquibaseException e) {
      log.error("{} :: error", command, e);
      throw e;
    }
    log.info("{} :: complete", command);
  }

}
