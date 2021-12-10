package com.example.spring.core.init;

import com.example.spring.core.beans.GreeterManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@SpringBootTest
@Slf4j
public class XmlAndAnnotationConfigTest {

  @Autowired
  private GreeterManager greeterManager;
  @Autowired
  private String appVersion;

  @Configuration
  @ComponentScan(basePackages = "com.example.spring")
  @ImportResource("classpath:/contexts/applicationContext-xmlAndAnnotations.xml")
  static class Config {

  }

  @Test
  public void exampleTest() {
    log.info("appVersion: {}", appVersion);
    log.info("greeterManager: {}", greeterManager);
    greeterManager.greet();
    greeterManager.fancyGreet();
  }

}
