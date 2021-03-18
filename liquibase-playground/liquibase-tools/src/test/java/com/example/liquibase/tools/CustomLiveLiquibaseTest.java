package com.example.liquibase.tools;

import com.example.liquibase.tools.config.CoreLiquibaseConfiguration;
import com.example.liquibase.tools.config.LiquibaseTaskProperties;
import com.example.liquibase.tools.config.LiquibaseTaskProperties.Command;
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
  private Liquibase liquibase;
  @Autowired
  private LiquibaseTaskProperties taskProperties;
  private PrintWriter writer = new PrintWriter(System.out);

  @Test
  void custom() throws LiquibaseException {
    Command command = Command.VALIDATE;

    if (command == Command.VALIDATE) {
      liquibase.validate();
    }
    if (command == Command.STATUS) {
      liquibase.reportStatus(
          taskProperties.isVerbose(), taskProperties.getContexts(), writer);
    }
    if (command == Command.UPDATE) {
      liquibase.update(
          taskProperties.getContexts());
    }
    if (command == Command.CLEAR_CHECKSUM) {
      liquibase.clearCheckSums();
    }
    if (command == Command.CHANGE_LOG_SYNC) {
      liquibase.changeLogSync(
          taskProperties.getContexts(), taskProperties.getLabels());
    }
  }

}
