package org.example.spring.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.example.spring.data.config.DataPopulatorConfig;
import org.example.spring.data.model.Role;
import org.example.spring.data.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
@Import(DataPopulatorConfig.class)
@Slf4j
class RoleRepositoryTest {
  @Autowired
  RoleRepository roleRepository;

  @Test
  void findAll() {
    var values = Lists.newArrayList(roleRepository.findAll());
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty();
  }

  @Test
  void findByName() {
    assertThat(roleRepository.findByValue("USER"))
        .isPresent()
        .get()
        .extracting(Role::getId)
        .isEqualTo(1L);
  }
}