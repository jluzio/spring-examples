package com.example.spring.core.config.enable_import;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
class EnableDataSourceTest {

  @Configuration
  @EnableDataSource
  static class Config {

  }

  @Autowired(required = false)
  @Qualifier("meaningOfLifeDataSource")
  String dataSource;

  @Test
  void test() {
    assertThat(dataSource).isNotNull();
  }
}
