package com.example.spring.batch.playground;

import com.example.spring.batch.playground.guide.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class BatchTest {

  @Autowired
  private PersonRepository personRepository;

  @Test
  void test() {
  }

}
