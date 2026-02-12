package com.example.liquibase.tools.task;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.liquibase.tools.config.LiquibaseTasksProperties;
import com.example.liquibase.tools.model.Task;
import com.example.liquibase.tools.model.Task.Command;
import com.example.liquibase.tools.service.LiquibaseDataService;
import com.example.liquibase.tools.service.LiquibaseFactory;
import com.example.liquibase.tools.service.LiquibaseTasksService;
import java.io.Writer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = LiquibaseTasksService.class)
@EnableConfigurationProperties(LiquibaseTasksProperties.class)
class LiquibaseTasksServiceTest {

  @Autowired
  private LiquibaseTasksService liquibaseTasksService;
  @Autowired
  private LiquibaseTasksProperties tasksProperties;
  @MockitoBean
  private LiquibaseFactory liquibaseFactory;
  @MockitoBean
  private Liquibase liquibase;
  @MockitoBean
  private LiquibaseDataService liquibaseDataService;

  @BeforeEach
  void setup() {
    when(liquibaseFactory.get(any()))
        .thenReturn(liquibase);
  }

  @Test
  @SuppressWarnings("java:S6073")
  void execute_success() throws LiquibaseException {
    setTasks(Command.VALIDATE, Command.STATUS, Command.CLEAR_CHANGE_LOG);
    liquibaseTasksService.execute();

    verify(liquibase).validate();
    verify(liquibase).reportStatus(anyBoolean(), anyContext(), anyLabels(), any());
    verify(liquibaseDataService).clearChangeLog();
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  void execute_validate() throws LiquibaseException {
    setTasks(Command.VALIDATE);
    liquibaseTasksService.execute();
    verify(liquibase).validate();
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  @SuppressWarnings("java:S6073")
  void execute_status() throws LiquibaseException {
    setTasks(Command.STATUS);
    liquibaseTasksService.execute();
    verify(liquibase).reportStatus(anyBoolean(), anyContext(), anyLabels(), any());
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  void execute_update() throws LiquibaseException {
    setTasks(Command.UPDATE);
    liquibaseTasksService.execute();
    verify(liquibase).update(anyContext(), anyLabels());
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  void execute_updateSQL() throws LiquibaseException {
    setTasks(Command.UPDATE_SQL);
    liquibaseTasksService.execute();
    verify(liquibase).update(anyContext(), anyLabels(), anyWriter());
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  void execute_changeLogSync() throws LiquibaseException {
    setTasks(Command.CHANGE_LOG_SYNC);
    liquibaseTasksService.execute();
    verify(liquibase).changeLogSync(anyContext(), anyLabels());
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  void execute_changeLogSyncSQL() throws LiquibaseException {
    setTasks(Command.CHANGE_LOG_SYNC_SQL);
    liquibaseTasksService.execute();
    verify(liquibase).changeLogSync(anyContext(), anyLabels(), anyWriter());
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  void execute_clearChangeLog() throws LiquibaseException {
    setTasks(Command.CLEAR_CHANGE_LOG);
    liquibaseTasksService.execute();
    verify(liquibaseDataService).clearChangeLog();
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  void execute_clearChecksum() throws LiquibaseException {
    setTasks(Command.CLEAR_CHECKSUM);
    liquibaseTasksService.execute();
    verify(liquibase).clearCheckSums();
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  void execute_forceReleaseLocks() throws LiquibaseException {
    setTasks(Command.FORCE_RELEASE_LOCKS);
    liquibaseTasksService.execute();
    verify(liquibase).forceReleaseLocks();
    verifyNoMoreInteractions(liquibase, liquibaseDataService);
  }

  @Test
  void execute_error() throws LiquibaseException {
    setTasks(Command.VALIDATE, Command.STATUS, Command.CLEAR_CHANGE_LOG);
    doThrow(new LiquibaseException("Database error!!!"))
        .when(liquibase).validate();

    assertThatThrownBy(
        () -> liquibaseTasksService.execute())
        .isInstanceOf(RuntimeException.class);

    verify(liquibase).validate();
    verify(liquibase, times(0)).reportStatus(anyBoolean(), Mockito.<Contexts>any(), any());
  }

  private void setTasks(Command... commands) {
    tasksProperties.setTasks(
        Stream.of(commands)
            .map(command ->
                Task.builder()
                    .command(command)
                    .build())
            .collect(Collectors.toList()));
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