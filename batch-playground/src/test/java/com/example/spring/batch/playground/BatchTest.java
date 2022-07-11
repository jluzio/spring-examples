package com.example.spring.batch.playground;

import com.example.spring.batch.playground.user_posts.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class BatchTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void test() {
  }

}
