package com.example.spring.framework.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SpringBootTest(
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "debug=true",
        "logging.level.root=DEBUG",
        "logging.level.org.springframework=DEBUG"
    }
)
@Slf4j
class OverrideTest {

  record Item(String id) {

  }

  interface Foo {

    String bar();
  }


  @Configuration
  @Order(1)
  static class Config {

    @Component("masterFoo")
    static class MasterFoo implements Foo {

      @Override
      public String bar() {
        return "master-foo-bar";
      }
    }

    @Component("discipleFoo")
    static class DiscipleFooFoo implements Foo {

      @Override
      public String bar() {
        return "disciple-foo-bar";
      }
    }
  }

  @Configuration
  @Order(2)
  static class OverrideConfig {

    @Component("masterFoo")
    static class MasterFoo implements Foo {

      @Override
      public String bar() {
        return "kung-foo-bar";
      }
    }
  }

  @Autowired
  ApplicationContext context;
  @Autowired
  List<Foo> foos;
  @Autowired
  Foo masterFoo;


  @Test
  void test() {
    log.info("beans: {}", Arrays.stream(context.getBeanDefinitionNames()).toList());

    log.info("foos: {}", foos);
    assertThat(foos)
        .map(Foo::bar)
        .containsExactlyInAnyOrder("kung-foo-bar", "disciple-foo-bar");
    assertThat(Stream.of(masterFoo))
        .map(Foo::bar)
        .containsExactly("kung-foo-bar");
  }
}
