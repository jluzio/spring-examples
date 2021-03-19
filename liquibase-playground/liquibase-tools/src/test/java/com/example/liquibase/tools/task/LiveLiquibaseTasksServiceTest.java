package com.example.liquibase.tools.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.example.liquibase.tools.LiveTestSupport;
import com.example.liquibase.tools.config.CoreLiquibaseConfiguration;
import com.example.liquibase.tools.service.LiquibaseTasksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@SpringBootTest
@EnabledIf(LiveTestSupport.LIVE_TEST_ENABLE_RULE)
@Import(CoreLiquibaseConfiguration.class)
class LiveLiquibaseTasksServiceTest {

  @Autowired
  private LiquibaseTasksService liquibaseTasksService;

  @Test
  void execute() {
    assertDoesNotThrow(() -> liquibaseTasksService.execute());
  }

}
