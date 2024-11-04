package com.example.spring.data.messages.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.messages.model.Message;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Slf4j
class MessageRepositoryDataJpaTest {

  @Autowired
  MessageRepository repository;
  @Autowired
  EntityManager entityManager;

  @Test
  void test() {
    var message = Message.builder()
        .id(UUID.randomUUID().toString())
        .text("Some message")
        .build();
    repository.save(message);

    var values = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty();
  }

}