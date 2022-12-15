package com.example.spring.core.init;

import com.example.spring.core.beans.GreeterManager;
import com.example.spring.core.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@SpringBootTest
@Slf4j
class XmlAndAnnotationConfigTest {

  @Autowired
  private GreeterManager greeterManager;
  @Autowired
  private String appVersion;

  @Configuration
  @Import(AppConfig.class)
  @ComponentScan(
      value = "com.example.spring.core.beans",
      excludeFilters = @Filter(
          type = FilterType.REGEX,
          pattern = ".*Test.*")
  )
  @ImportResource("classpath:/contexts/applicationContext-xmlAndAnnotations.xml")
  static class Config {

  }

  @Test
  void exampleTest() {
    log.info("appVersion: {}", appVersion);
    log.info("greeterManager: {}", greeterManager);
    greeterManager.greet();
    greeterManager.fancyGreet();
  }

}
