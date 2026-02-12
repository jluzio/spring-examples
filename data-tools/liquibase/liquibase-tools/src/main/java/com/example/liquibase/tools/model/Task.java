package com.example.liquibase.tools.model;

import liquibase.Contexts;
import liquibase.LabelExpression;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

  public enum Command {
    VALIDATE, STATUS, FORCE_RELEASE_LOCKS,
    CLEAR_CHECKSUM, CHANGE_LOG_SYNC, CHANGE_LOG_SYNC_SQL,
    UPDATE, UPDATE_SQL, CLEAR_CHANGE_LOG
  }

  public enum OutputMode {
    LOG, STDOUT, STDERR
  }

  private Command command;
  private String changeLog;
  private Contexts contexts;
  private LabelExpression labels;
  @Builder.Default
  private OutputMode output = OutputMode.LOG;
  private boolean verbose;

  public void setContexts(String... contexts) {
    this.contexts = new Contexts(contexts);
  }

  public void setLabels(String... labels) {
    this.labels = new LabelExpression(labels);
  }

}
