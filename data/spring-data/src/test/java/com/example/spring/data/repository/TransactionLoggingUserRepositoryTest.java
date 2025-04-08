package com.example.spring.data.repository;

import static com.example.spring.data.repository.Identifiers.generateUuid;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.jpa.config.DataPopulatorConfig;
import com.example.spring.data.jpa.model.Role;
import com.example.spring.data.jpa.model.User;
import com.example.spring.data.jpa.model.UserStatus;
import com.google.common.collect.Lists;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Subquery;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(DataPopulatorConfig.class)
@Slf4j
@ActiveProfiles("transaction-logging")
class TransactionLoggingUserRepositoryTest {

  @Autowired
  UserRepository userRepository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  EntityManager entityManager;

  // ===========================================================================================
  // NOTE: Using SpringBootTest, since DataJpaTest creates a transaction for the test method
  // ===========================================================================================


  @Test
  void multiple_repo_calls_multiple_transactions() {
    multiple_repo_calls_scenario();
  }

  @Test
  @Transactional
  void multiple_repo_calls_single_transaction() {
    multiple_repo_calls_scenario();
  }

  void multiple_repo_calls_scenario() {
    log.debug("=== start ===");

    var user = new User();
    String id = generateUuid();
    user.setId(id);
    user.setName("custom");
    user.setEmail("custom@mail.org");
    user.setRole(new Role("1"));
    user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
    log.debug("{}", user);

    log.debug("=== save ===");
    userRepository.save(user);
    assertThat(user.getId())
        .isNotNull()
        .isEqualTo(id);

    log.debug("=== findById ===");
    assertThat(userRepository.findById(user.getId()))
        .isPresent()
        .get()
        .hasFieldOrPropertyWithValue("id", id);

    log.debug("=== end ===");
  }


}