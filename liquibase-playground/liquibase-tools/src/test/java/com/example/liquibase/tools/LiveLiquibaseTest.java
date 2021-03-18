package com.example.liquibase.tools;

import com.example.liquibase.tools.config.CoreLiquibaseConfiguration;
import com.example.liquibase.tools.config.LiquibaseTaskProperties;
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
class LiveLiquibaseTest {

  @Autowired
  private Liquibase liquibase;
  @Autowired
  private LiquibaseTaskProperties taskProperties;
  private PrintWriter writer = new PrintWriter(System.out);

  @Test
  void validate() throws LiquibaseException {
    liquibase.reportStatus(
        taskProperties.isVerbose(), taskProperties.getContexts(), writer);
  }

  @Test
  void status() throws LiquibaseException {
    liquibase.reportStatus(
        taskProperties.isVerbose(), taskProperties.getContexts(), writer);
  }

  @Test
  void updateSQL() throws LiquibaseException {
    liquibase.update(
        taskProperties.getContexts(), writer);
  }

  @Test
  void changeLogSQL() throws LiquibaseException {
    liquibase.changeLogSync(
        taskProperties.getContexts(), taskProperties.getLabels(), writer);
  }

  @Test
  void forceReleaseLocks() throws LiquibaseException {
    liquibase.forceReleaseLocks();
  }

}
