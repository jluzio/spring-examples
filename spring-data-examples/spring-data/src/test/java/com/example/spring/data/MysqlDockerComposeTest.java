package com.example.spring.data;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.jpa.config.DataPopulatorConfig;
import com.example.spring.data.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(DataPopulatorConfig.class)
@ActiveProfiles("mysql-test")
@Log4j2
class MysqlDockerComposeTest {

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
