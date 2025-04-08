package com.example.spring.core.environment.conditional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
class ConditionalOnMissingBeanTest {

  @Configuration
  @Import({SomeServiceA.class, SomeServiceB.class, SomeServiceC.class})
  static class Config {

  }

  @Autowired
  List<SomeService> someServiceList;

  @Test
  void test() {
    log.debug(someServiceList.toString());
    assertThat(someServiceList)
        .hasSize(1)
        .element(0)
        .isInstanceOf(SomeServiceA.class);
  }


  interface SomeService {

  }

  @Component
  @ConditionalOnMissingBean(SomeService.class)
  static class SomeServiceA implements SomeService {

  }

  @Component
  @ConditionalOnMissingBean(SomeService.class)
  static class SomeServiceB implements SomeService {

  }

  @Component
  @ConditionalOnMissingBean(SomeService.class)
  static class SomeServiceC implements SomeService {

  }

}
