package com.example.spring.data.messages.repository;

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
class MessageRepositorySpringBootTest {

  @Autowired
  MessageRepository repository;
  @Autowired
  @Qualifier("messagesEntityManagerFactory")
  EntityManager entityManager;

  @Test
  void test() {
    Metamodel metamodel = entityManager.getMetamodel();
    assertThat(metamodel.managedType(Message.class))
        .isNotNull();
    assertThatThrownBy(() -> metamodel.managedType(Todo.class))
        .isInstanceOf(IllegalArgumentException.class);

    var message = Message.builder()
        .id(UUID.randomUUID().toString())
        .text("Some message")
        .build();
    repository.save(message);

    assertThat(entityManager.find(Message.class, message.getId()))
        .isNotNull();

    var values = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty();
  }

}