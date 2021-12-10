package com.example.spring.core.config.imports;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
public class ImportResourceTest {

  @Autowired
  List<String> beans;

  @Test
  void test() {
    log.info("beans: {}", beans);
  }

  @Configuration
  @ImportResource("classpath:/contexts/applicationContext-importResource.xml")
  static class Config {

    @Value("${jdbc.url}")
    private String url;

    @Bean
    String dataSourceUrl() {
      return url;
    }
  }

  @Component
  class DataSource {

  }

}
