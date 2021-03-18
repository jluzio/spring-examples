package com.example.liquibase.tools.task;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.liquibase.tools.config.LiquibaseTaskProperties;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(
    classes = LiquibaseTask.class,
    properties = {
        "liquibase.task.commands=VALIDATE,STATUS",
        "liquibase.task.verbose=true"
    }
)
@EnableConfigurationProperties(LiquibaseTaskProperties.class)
class LiquibaseTaskTest {

  @Autowired
  private LiquibaseTask liquibaseTask;
  @MockBean
  private Liquibase liquibase;

  @Test
  void execute_success() throws LiquibaseException {
    doNothing()
        .when(liquibase).validate();
    doNothing()
        .when(liquibase).reportStatus(anyBoolean(), Mockito.<Contexts>any(), any());

    liquibaseTask.execute();

    verify(liquibase).validate();
    verify(liquibase).reportStatus(anyBoolean(), Mockito.<Contexts>any(), any());
  }

  @Test
  void execute_error() throws LiquibaseException {
    doThrow(new LiquibaseException("Database error!!!"))
        .when(liquibase).validate();

    assertThatThrownBy(
        () -> liquibaseTask.execute())
        .isInstanceOf(RuntimeException.class);

    verify(liquibase).validate();
    verify(liquibase, times(0)).reportStatus(anyBoolean(), Mockito.<Contexts>any(), any());
  }
}