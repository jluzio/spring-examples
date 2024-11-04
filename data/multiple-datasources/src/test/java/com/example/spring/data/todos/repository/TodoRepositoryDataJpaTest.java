package com.example.spring.data.todos.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.todos.model.Todo;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Slf4j
class TodoRepositoryDataJpaTest {

  @Autowired
  TodoRepository repository;
  @Autowired
  EntityManager entityManager;

  @Test
  void test() {
    var todo = Todo.builder()
        .id(UUID.randomUUID().toString())
        .title("Some todo")
        .completed(false)
        .build();
    repository.save(todo);

    var values = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty();
  }

}