package com.example.spring.data.mongodb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.mongodb.DockerImages;
import com.example.spring.data.mongodb.model.User;
import com.example.spring.data.mongodb.model.projection.UserReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
class UserRespositoryTestcontainersTest {

  @Container
  @ServiceConnection
  private static final MongoDBContainer container = new MongoDBContainer(DockerImages.MONGO_DB);
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository repository;

  Supplier<String> id = () -> UUID.randomUUID().toString();

  @BeforeEach
  void setup() {
    repository.deleteAll();
    // default users
    Stream.of("John", "Jane", "Tom")
        .map(firstName -> User.builder()
            .id(id.get())
            .name(firstName + " Doe")
            .username((firstName + "doe").toLowerCase())
            .build())
        .forEach(repository::save);
  }

  @Test
  void repository_write_read() {
    var user = User.builder()
        .id(id.get())
        .name("Johnny Doe")
        .username("johnnydoe")
        .build();
    repository.save(user);

    var searchResult = repository.findByUsername(user.getUsername());
    assertThat(searchResult.getId())
        .isEqualTo(user.getId());
  }

  @Test
  void repository_projection() {
    List<UserReference> searchResult = repository.findReferenceByName("John Doe");
    log.debug(searchResult);
    assertThat(searchResult)
        .isNotEmpty();
  }
}
