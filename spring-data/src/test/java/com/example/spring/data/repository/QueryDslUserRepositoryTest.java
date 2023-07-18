package com.example.spring.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.jpa.config.DataPopulatorConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({DataPopulatorConfig.class, QueryDslUserRepositoryImpl.class})
@Slf4j
class QueryDslUserRepositoryTest {

  @Autowired
  QueryDslUserRepository userRepository;

  @Test
  void findByNameOrderByCreatedAt() {
    assertThat(userRepository.findByNameOrderByCreatedAt("John Doe"))
        .isNotEmpty()
        .hasSize(1);
  }

}