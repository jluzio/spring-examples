package com.example.spring.data.redis.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.redis.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    properties = {
        "spring.data.redis.port=12345",
        "spring.redis.port=12345"
    }
)
@Testcontainers
@Log4j2
class UserRepositoryTestcontainersTest {

  @Container
  @ServiceConnection(name = "redis")
  static final RedisContainer redisContainer = new RedisContainer("redis:7-alpine");

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RedisConnectionDetails redisConnectionDetails;

  @Test
  void connectionDetails() {
    var standalone = redisConnectionDetails.getStandalone();
    log.debug("Redis :: {}:{}", standalone.getHost(), standalone.getPort());
    assertThat(standalone)
        .isNotNull()
        .satisfies(it -> assertThat(it.getHost()).isNotNull())
        .satisfies(it -> assertThat(it.getPort()).isNotZero());
  }

  @Test
  void basic() {
    var users = userRepository.findAll();
    log.info("users: {}", users);
    assertThat(users)
        .hasSize(3);

    var newUser = new User();
    newUser.setName("New Doe");

    userRepository.save(newUser);

    var updatedUsers = userRepository.findAll();
    log.info("updatedUsers: {}", updatedUsers);
    assertThat(updatedUsers)
        .hasSize(4);
  }

}
