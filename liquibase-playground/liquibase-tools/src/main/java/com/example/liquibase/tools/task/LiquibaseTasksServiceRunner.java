package com.example.liquibase.tools.task;

import com.example.liquibase.tools.service.LiquibaseTasksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("run-task")
@Slf4j
public class LiquibaseTasksServiceRunner implements CommandLineRunner {
  @Autowired
  private LiquibaseTasksService liquibaseTasksService;

  @Override
  public void run(String... args) throws Exception {
    log.info("Running LiquibaseTask");
    liquibaseTasksService.execute();
  }
}
