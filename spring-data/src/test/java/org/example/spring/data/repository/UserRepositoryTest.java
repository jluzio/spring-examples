package org.example.spring.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.example.spring.data.config.DataPopulatorConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(DataPopulatorConfig.class)
@Slf4j
class UserRepositoryTest {
  @Autowired
  UserRepository userRepository;

  @Test
  void findAll() {
    var values = Lists.newArrayList(userRepository.findAll());
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty();
  }

  @Test
  void findByName() {
    assertThat(userRepository.findByName("John Doe"))
        .isNotEmpty()
        .hasSize(1);
  }
}