package com.example.spring.data.repository;

import static com.example.spring.data.jpa.model.QUser.user;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.jpa.config.DataPopulatorConfig;
import com.example.spring.data.jpa.model.User;
import com.querydsl.core.Tuple;
import java.util.List;
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

  @Test
  void findIdAndEmail() {
    List<Tuple> idAndEmails = userRepository.findIdAndEmail();
    assertThat(idAndEmails)
        .isNotEmpty();
    idAndEmails.forEach(it -> {
      log.debug("{}", it);
      log.debug("id: {} | email: {}", it.get(user.id), it.get(user.email));
    });
  }

  @Test
  void findUsersWithLongestEmail() {
    assertThat(userRepository.findUsersWithLongestEmail())
        .satisfies(it -> log.debug("{}", it))
        .isNotEmpty()
        .extracting(User::getEmail)
        .containsExactly("tom.doe@othermail.org");
  }
}