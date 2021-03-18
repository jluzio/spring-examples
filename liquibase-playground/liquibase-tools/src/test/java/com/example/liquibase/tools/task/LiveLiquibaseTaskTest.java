package com.example.liquibase.tools.task;

import com.example.liquibase.tools.LiveTestSupport;
import com.example.liquibase.tools.config.CoreLiquibaseConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@SpringBootTest
@EnabledIf(LiveTestSupport.LIVE_TEST_ENABLE_RULE)
@Import(CoreLiquibaseConfiguration.class)
class LiveLiquibaseTaskTest {

  @Autowired
  private LiquibaseTask liquibaseTask;

  @Test
  void execute() {
    liquibaseTask.execute();
  }

}
