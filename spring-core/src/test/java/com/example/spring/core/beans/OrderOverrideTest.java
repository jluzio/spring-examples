package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.core.beans.OrderOverrideTest.OverrideConfig;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SpringBootTest(
    classes = {OrderOverrideTest.Config.class, OverrideConfig.class},
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "debug=true",
        "logging.level.root=DEBUG",
        "logging.level.org.springframework=DEBUG"
    }
)
@Slf4j
class OrderOverrideTest {

  record Item(String id) {

  }

  interface Foo {

    String bar();
  }


  @Configuration
  static class Config {

    @Bean
    @Order(2)
    Item item2() {
      return new Item("2");
    }

    @Bean
    @Order(1)
    Item item1() {
      return new Item("1");
    }

    @Bean
    Item item3() {
      return new Item("3");
    }

    @Component
    @Order(1)
    static class MasterFoo implements Foo {

      @Override
      public String bar() {
        return "master-foo-bar";
      }
    }

    @Component
    static class DiscipleFoo implements Foo {

      @Override
      public String bar() {
        return "disciple-foo-bar";
      }
    }
  }

  @Configuration
  static class OverrideConfig {

    @Bean
    Item item3() {
      return new Item("3-override");
    }

    @Component("com.example.spring.core.beans.OrderOverrideTest$Config$MasterFoo")
    @Order(1)
    static class MasterFoo implements Foo {

      @Override
      public String bar() {
        return "kung-foo-bar";
      }
    }

    // Does not get special treatment while getting all Foo instances
    // But can replace direct injection of DiscipleFoo
    @Component
    static class DiscipleFoo extends Config.DiscipleFoo {

      @Override
      public String bar() {
        return "trainee-foo-bar";
      }
    }
  }

  @Autowired
  ApplicationContext context;
  @Autowired
  List<Item> items;
  @Autowired
  List<Foo> foos;


  @Test
  void test() {
    log.info("beans: {}", Arrays.stream(context.getBeanDefinitionNames()).toList());

    // gives first beans with @Order/@Priority/impl Ordered, then others by order of registration
    log.info("items: {}", items);
    assertThat(items)
        .map(Item::id)
        .isEqualTo(List.of("1", "2", "3-override"));

    log.info("foos: {}", foos);
    assertThat(foos)
        .map(Foo::bar)
        .isEqualTo(List.of("kung-foo-bar", "disciple-foo-bar", "trainee-foo-bar"));
  }

}
