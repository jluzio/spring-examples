package com.example.spring.data.redis;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.redis.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("docker-compose-test")
@Log4j2
class DockerComposeTest {

  @Autowired
  private UserRepository repository;

  @Test
  void repository_check_populated() {
    var users = repository.findAll();
    log.debug("users: {}", users);
    assertThat(users)
        .hasSizeGreaterThan(1);
  }

}
