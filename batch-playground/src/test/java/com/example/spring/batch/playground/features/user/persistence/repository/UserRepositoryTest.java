package com.example.spring.batch.playground.features.user.persistence.repository;

import com.example.spring.batch.playground.persistence.IdGenerators;
import com.example.spring.batch.playground.features.user.persistence.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import reactor.core.publisher.Flux;

@DataJpaTest
@Slf4j
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void repository() {
    log.info("--- initial data ---");
    Flux.fromIterable(userRepository.findAll())
        .doOnNext(user -> log.info("person: {}", user))
        .blockLast();

    log.info("--- saving new user ---");
    User newUser = User.builder()
        .id(IdGenerators.uuid())
        .name("Test")
        .username("test")
        .email("test@mail.org")
        .build();
    userRepository.save(newUser);

    log.info("--- final data ---");
    Flux.fromIterable(userRepository.findAll())
        .doOnNext(user -> log.info("person: {}", user))
        .blockLast();

    log.info("done");
  }

}
