package com.example.spring.framework.beans;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootTest(
    classes = CircularReferenceWithConfigTest.Config.class,
    properties = {
        "greeter.name=Circle",
        "spring.main.allow-circular-references=true",
    }
)
@EnableConfigurationProperties
class CircularReferenceWithConfigTest {

  static class Config {

    @Bean
    @ConfigurationProperties("greeter")
    Greeter greeter() {
      return new Greeter();
    }

    @Autowired
    void enhanceGreeter(Greeter greeter) {
      greeter.setName("Mr %s".formatted(greeter.getName()));
    }

  }

  @Data
  static class Greeter {

    private String name;

    public void sayHello() {
      log.info("Hello {}!", name);
    }
  }

  @Autowired
  Greeter greeter;

  @Test
  void test() {
    greeter.sayHello();
  }

}
