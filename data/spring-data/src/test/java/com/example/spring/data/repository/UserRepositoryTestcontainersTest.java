package com.example.spring.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.DockerImages;
import com.example.spring.data.jpa.config.DataPopulatorConfig;
import com.example.spring.data.jpa.model.Role;
import com.example.spring.data.jpa.model.User;
import com.google.common.collect.Lists;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Subquery;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

//@SpringBootTest
@DataJpaTest(properties = {
    "spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=drop-and-create",
    "spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=build/classes/java/test/gen/create.sql",
    "spring.jpa.properties.jakarta.persistence.schema-generation.scripts.drop-target=build/classes/java/test/gen/drop.sql",
    "spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-source=metadata",
    "spring.jpa.properties.jakarta.persistence.schema-generation.scripts.drop-source=metadata"
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
@Import(DataPopulatorConfig.class)
@Slf4j
class UserRepositoryTestcontainersTest {

  @Container
  @ServiceConnection
  static final MySQLContainer<?> MYSQL_CONTAINER =
      new MySQLContainer<>(DockerImages.MYSQL)
          .withDatabaseName("testdb")
          .withUsername("root")
          .withPassword("testpass")
          .withInitScript("schemas/mysql/drop.sql")
          .withInitScript("schemas/mysql/create.sql")
      ;

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
  void saveUser() {
    var user = new User();
    user.setName("custom");
    user.setEmail("custom@mail.org");
    user.setRole(new Role(1L));
    user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
    log.debug("{}", user);

    userRepository.save(user);
    assertThat(user.getId())
        .isNotNull()
        .isEqualTo(4L);

    assertThat(userRepository.findById(4L))
        .isPresent()
        .hasValue(user);
  }

  @Test
  void findByName() {
    Assertions.assertThat(userRepository.findByName("John Doe"))
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  void specification_basic() {
    var users = userRepository.findAll(emailEndsWith("@mail.org"));
    var values = Lists.newArrayList(users);
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty()
        .hasSize(2)
        .extracting("id")
        .containsExactly(1L, 2L)
    ;
  }

  @Test
  void specification_and_sort() {
    var users = userRepository.findAll(
        emailEndsWith("org").and(nameStartsWith("J")),
        Sort.by(Direction.DESC, "id")
    );
    var values = Lists.newArrayList(users);
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty()
        .hasSize(2)
        .extracting("id")
        .containsExactly(2L, 1L)
    ;
  }

  @Test
  void specification_with_subquery() {
    var users = userRepository.findAll(roleIdIsInRolesWithValue("ADMIN"));
    var values = Lists.newArrayList(users);
    log.debug("values: {}", values);
    assertThat(values)
        .isNotEmpty()
        .hasSize(1)
        .extracting("id")
        .containsExactly(3L)
    ;
  }

  private Specification<User> emailEndsWith(String value) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(root.get("email"), String.format("%%%s", value));
  }

  private Specification<User> nameStartsWith(String value) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(root.get("name"), String.format("%s%%", value));
  }

  private Specification<User> roleIdIsInRolesWithValue(String roleValue) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder
            .in(root.<Role>get("role").<Long>get("id"))
            .value(roleIdSubquery(query, criteriaBuilder, roleValue));
  }

  private Subquery<Long> roleIdSubquery(CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
      String roleValue) {
    var subquery = query.subquery(Long.class);
    var root = subquery.from(Role.class);
    return subquery
        .where(criteriaBuilder.equal(root.get("value"), roleValue))
        .select(root.get("id"));
  }
}