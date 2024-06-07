package com.example.spring.data.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class MongodbTestcontainersTest {

  @Container
  @ServiceConnection
  private static final MongoDBContainer container = new MongoDBContainer("mongo:latest");
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserMongoRepository repository;

  Supplier<String> id = () -> UUID.randomUUID().toString();

  @BeforeEach
  void setup() {
    repository.deleteAll();
    // default users
    Stream.of("John", "Jane", "Tom")
        .map(firstName -> User.builder()
            .id(id.get())
            .firstName(firstName)
            .lastName("Doe")
            .username((firstName + "doe").toLowerCase())
            .build())
        .forEach(repository::save);
  }

  @Test
  void repository_write_read() {
    var user = User.builder()
        .id(id.get())
        .firstName("Johhny")
        .lastName("Doe")
        .username("johnnydoe")
        .build();
    repository.save(user);

    var searchResult = repository.findByUsername(user.getUsername());
    assertThat(searchResult.getId())
        .isEqualTo(user.getId());
  }
}
