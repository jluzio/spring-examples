package com.example.spring.cdi.environment;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Slf4j
@ActiveProfiles("env_testing")
public class EnvironmentTest {

  @Autowired
  Environment environment;

  @Test
  void test() {
    log.info("environment info :: p:{} | test prop:{} | test prop csv:{} | application.properties:{}",
        environment.getActiveProfiles(),
        environment.getProperty("app.test"),
        Arrays.asList(environment.getProperty("app.test.csv", Integer[].class)),
        environment.getProperty("logging.level.com.example.spring"));
  }

  @Configuration
  @PropertySources({
      @PropertySource("classpath:/test.properties")
  })
  static class Config {

  }

}
