package com.example.spring.framework.beans;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@SpringBootTest(
    properties = "targetBean.propValue=somePropValue"
)
class ConstructorInjectionTest {

  record Dep(String id) {

  }

  @Data
  @RequiredArgsConstructor
  static class TargetBean {

    private final Dep dep1;
    // lombok is copying annotation to constructor
    @Qualifier("otherDep")
    private final Dep dep2;
    // lombok is copying annotation to constructor
    @Value("${targetBean.propValue}")
    private final String propValue;

  }

  @Configuration
  @Import(TargetBean.class)
  static class Config {

    @Bean
    @Primary
    Dep defaultDep() {
      return new Dep("defaultDep");
    }

    @Bean
    Dep otherDep() {
      return new Dep("otherDep");
    }
  }

  @Autowired
  TargetBean targetBean;

  @Test
  void test() {
    assertThat(targetBean.getDep1().id())
        .isEqualTo("defaultDep");
    assertThat(targetBean.getDep2().id())
        .isEqualTo("otherDep");
    assertThat(targetBean.getPropValue())
        .isEqualTo("somePropValue");
  }

}
