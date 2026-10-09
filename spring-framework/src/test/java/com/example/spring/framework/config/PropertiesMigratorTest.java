package com.example.spring.framework.config;

import static org.assertj.core.api.Assertions.assertThatNoException;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.main.show-banner: true")
@Slf4j
class PropertiesMigratorTest {

  @Test
  void test() {
    assertThatNoException()
        .isThrownBy(() -> log.info("Warning of property 'spring.main.show-banner' should be present in log"));
  }

}
