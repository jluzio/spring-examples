package com.example.spring.framework.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Slf4j
class ReplaceConfigTest {

  @Autowired
  @Qualifier("appVersion")
  Optional<String> appVersion;

  @Test
  void test() {
    log.info("appVersion: {}", appVersion);
    assertThat(appVersion)
        .isEmpty();
  }

  @Configuration
  static class Config {

  }

}
