package com.example.liquibase.tools.config;

import java.util.List;
import liquibase.Contexts;
import liquibase.LabelExpression;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("liquibase.task")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquibaseTaskProperties {

  public enum Command {
    VALIDATE, STATUS, FORCE_RELEASE_LOCKS,
    CLEAR_CHECKSUM, CHANGE_LOG_SYNC, CHANGE_LOG_SYNC_SQL,
    UPDATE, UPDATE_SQL}

  private List<Command> commands;
  private Contexts contexts;
  private LabelExpression labels;
  private boolean verbose;

  public void setContexts(String... contexts) {
    this.contexts = new Contexts(contexts);
  }

  public void setLabels(String... labels) {
    this.labels = new LabelExpression(labels);
  }

}
