package com.example.spring.ldap.repository;

import com.example.spring.ldap.model.User;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class UserRepositoryTest {

  @Autowired
  UserRepository repository;

  @Test
  void test() {
    List<User> users = repository.findAll();
    log.debug("users: {}", users);
  }

}