package com.example.liquibase.tools.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.liquibase.tools.config.LiquibaseTaskProperties.Command;
import java.util.List;
import liquibase.Contexts;
import liquibase.LabelExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {LiquibaseTaskProperties.class},
    properties = {
        "liquibase.task.commands=VALIDATE,STATUS",
        "liquibase.task.contexts=c1,c2",
        "liquibase.task.labels=l1,l2",
        "liquibase.task.verbose=true"
    }
)
@EnableConfigurationProperties
class LiquibaseTaskPropertiesTest {

  @Autowired
  LiquibaseTaskProperties properties;

  @Test
  void test() {
    assertThat(properties)
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(LiquibaseTaskProperties.builder()
            .commands(List.of(Command.VALIDATE, Command.STATUS))
            .contexts(new Contexts("c1", "c2"))
            .labels(new LabelExpression("l1", "l2"))
            .verbose(true)
            .build());
  }

}