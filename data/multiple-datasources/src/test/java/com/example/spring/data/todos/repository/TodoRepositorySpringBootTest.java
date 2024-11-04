package com.example.spring.data.todos.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.spring.data.messages.model.Message;
import com.example.spring.data.todos.model.Todo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Metamodel;
import java.util.UUID;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class TodoRepositorySpringBootTest {

  @Autowired
  TodoRepository repository;
  @Autowired
  @Qualifier("todosEntityManagerFactory")
  EntityManager entityManager;

  @Test
  void test() {
    Metamodel metamodel = entityManager.getMetamodel();
    assertThat(metamodel.managedType(Todo.class))
        .isNotNull();
    assertThatThrownBy(() -> metamodel.managedType(Message.class))
        .isInstanceOf(IllegalArgumentException.class);

    var todo = Todo.builder()
        .id(UUID.randomUUID().toString())
        .title("Some todo")
        .completed(false)
        .build();
    repository.save(todo);

    assertThat(entityManager.find(Todo.class, todo.getId()))
        .isNotNull();

    var values = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty();
  }

}