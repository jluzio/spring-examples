package com.example.liquibase.tools.task;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.example.liquibase.tools.config.LiquibaseTaskProperties;
import com.example.liquibase.tools.config.LiquibaseTaskProperties.Command;
import com.example.liquibase.tools.repository.DatabaseChangelogRepository;
import java.io.Writer;
import java.util.List;
import liquibase.Contexts;
import liquibase.LabelExpression;
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
        "liquibase.task.commands=VALIDATE,STATUS,CLEAR_CHANGE_LOG",
        "liquibase.task.verbose=true"
    }
)
@EnableConfigurationProperties(LiquibaseTaskProperties.class)
class LiquibaseTaskTest {

  @Autowired
  private LiquibaseTask liquibaseTask;
  @Autowired
  private LiquibaseTaskProperties taskProperties;
  @MockBean
  private Liquibase liquibase;
  @MockBean
  private DatabaseChangelogRepository changelogRepository;


  @Test
  void execute_success() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.VALIDATE, Command.STATUS, Command.CLEAR_CHANGE_LOG));
    liquibaseTask.execute();

    verify(liquibase).validate();
    verify(liquibase).reportStatus(anyBoolean(), anyContext(), anyLabels(), any());
    verify(changelogRepository).deleteAll();
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_validate() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.VALIDATE));
    liquibaseTask.execute();
    verify(liquibase).validate();
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_status() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.STATUS));
    liquibaseTask.execute();
    verify(liquibase).reportStatus(anyBoolean(), anyContext(), anyLabels(), any());
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_update() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.UPDATE));
    liquibaseTask.execute();
    verify(liquibase).update(anyContext(), anyLabels());
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_updateSQL() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.UPDATE_SQL));
    liquibaseTask.execute();
    verify(liquibase).update(anyContext(), anyLabels(), anyWriter());
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_changeLogSync() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.CHANGE_LOG_SYNC));
    liquibaseTask.execute();
    verify(liquibase).changeLogSync(anyContext(), anyLabels());
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_changeLogSyncSQL() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.CHANGE_LOG_SYNC_SQL));
    liquibaseTask.execute();
    verify(liquibase).changeLogSync(anyContext(), anyLabels(), anyWriter());
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_clearChangeLog() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.CLEAR_CHANGE_LOG));
    liquibaseTask.execute();
    verify(changelogRepository).deleteAll();
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_clearChecksum() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.CLEAR_CHECKSUM));
    liquibaseTask.execute();
    verify(liquibase).clearCheckSums();
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_forceReleaseLocks() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.FORCE_RELEASE_LOCKS));
    liquibaseTask.execute();
    verify(liquibase).forceReleaseLocks();
    verifyNoMoreInteractions(liquibase, changelogRepository);
  }

  @Test
  void execute_error() throws LiquibaseException {
    taskProperties.setCommands(List.of(Command.VALIDATE, Command.STATUS, Command.CLEAR_CHANGE_LOG));
    doThrow(new LiquibaseException("Database error!!!"))
        .when(liquibase).validate();

    assertThatThrownBy(
        () -> liquibaseTask.execute())
        .isInstanceOf(RuntimeException.class);

    verify(liquibase).validate();
    verify(liquibase, times(0)).reportStatus(anyBoolean(), Mockito.<Contexts>any(), any());
  }

  private Contexts anyContext() {
    return Mockito.any();
  }

  private LabelExpression anyLabels() {
    return Mockito.any();
  }

  private Writer anyWriter() {
    return Mockito.any();
  }
}