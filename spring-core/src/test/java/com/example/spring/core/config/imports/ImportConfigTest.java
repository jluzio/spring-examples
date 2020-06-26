package com.example.spring.core.config.imports;

import com.example.spring.core.config.imports.ImportConfigTest.Config1;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = Config1.class)
@Slf4j
public class ImportConfigTest {
  @Autowired
  List<String> beans;

  @Test
  void test() {
    log.info("beans: {}", beans);
  }

  @Configuration
  @Import(Config2.class)
  static class Config1 {

    @Bean
    String bean1() {
      return "bean1";
    }
  }

  @Configuration
  static class Config2 {

    @Bean
    String bean2() {
      return "bean2";
    }
  }

  @Configuration
  static class Config3 {

    @Bean
    String bean3() {
      return "bean3";
    }
  }

}
