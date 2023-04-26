package com.example.liquibase.tools.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.liquibase.tools.domain.Databasechangelog;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Slf4j
class DatabaseChangeLogRepositoryTest {
  @Autowired
  private DatabaseChangeLogRepository repository;

  @Test
  void test() {
    assertThat(repository.findAll())
        .isEmpty();

    Databasechangelog changelog = Databasechangelog.builder()
        .id("1")
        .filename("file1")
        .author("author1")
        .comments("test changelog")
        .build();
    repository.save(changelog);

    assertThat(repository.findAll())
        .hasSize(1)
        .containsExactly(changelog);
  }

}