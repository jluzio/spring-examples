package com.example.liquibase.tools;

import com.example.liquibase.tools.config.CoreLiquibaseConfiguration;
import com.example.liquibase.tools.config.LiquibaseTasksProperties;
import com.example.liquibase.tools.model.Task;
import com.example.liquibase.tools.model.Task.Command;
import com.example.liquibase.tools.service.LiquibaseDataService;
import com.example.liquibase.tools.service.LiquibaseFactory;
import java.io.PrintWriter;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@SpringBootTest
@EnabledIf(LiveTestSupport.LIVE_TEST_ENABLE_RULE)
@Import(CoreLiquibaseConfiguration.class)
class CustomLiveLiquibaseTest {

  @Autowired
  private LiquibaseFactory liquibaseFactory;
  @Autowired
  private LiquibaseTasksProperties tasksProperties;
  @Autowired
  private LiquibaseDataService liquibaseDataService;
  private final PrintWriter writer = new PrintWriter(System.out);

  @Test
  void custom() throws LiquibaseException {
    Liquibase liquibase = liquibaseFactory.get(tasksProperties.getDefaultChangeLog());
    Task task = tasksProperties.getTasks().get(0);
    Command command = Command.CHANGE_LOG_SYNC;

    if (command == Command.VALIDATE) {
      liquibase.validate();
    }
    if (command == Command.STATUS) {
      liquibase.reportStatus(
          task.isVerbose(), task.getContexts(), writer);
    }
    if (command == Command.UPDATE) {
      liquibase.update(
          task.getContexts());
    }
    if (command == Command.CLEAR_CHECKSUM) {
      liquibase.clearCheckSums();
    }
    if (command == Command.CHANGE_LOG_SYNC) {
      liquibase.changeLogSync(
          task.getContexts(), task.getLabels());
    }
    if (command == Command.CLEAR_CHANGE_LOG) {
      liquibaseDataService.clearChangeLog();
    }
  }

}
