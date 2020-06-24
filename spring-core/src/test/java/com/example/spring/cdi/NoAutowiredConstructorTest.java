package com.example.spring.cdi;

import com.example.spring.cdi.NoAutowiredConstructorTest.Config.SomeBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
public class NoAutowiredConstructorTest {

  @TestConfiguration
  static class Config {
    @Component
    @Data
    public static class SomeBean {

      private String appVersion;

      public SomeBean(String appVersion) {
        this.appVersion = appVersion;
      }
    }
  }

  @Autowired
  private SomeBean someBean;

  @Test
  void test() {
    log.info("someBean: {}", someBean);
  }

}
