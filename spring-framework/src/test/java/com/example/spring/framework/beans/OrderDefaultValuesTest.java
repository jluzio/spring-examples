package com.example.spring.framework.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@SpringBootTest
@Slf4j
class OrderDefaultValuesTest {

  record Item(String id) {

  }

  @Configuration
  static class Config {

    @Bean
    @Order(-10)
    Item itemM10() {
      return new Item("-10");
    }

    @Bean
    @Order(10)
    Item itemP10() {
      return new Item("10");
    }

    @Bean
    @Order(0)
    Item item0() {
      return new Item("0");
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    Item itemHP() {
      return new Item("hp");
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    Item itemLP() {
      return new Item("lp");
    }

    @Bean
    Item itemDefault() {
      return new Item("default");
    }
  }

  @Autowired
  List<Item> items;


  @Test
  void test() {
    // gives first beans with @Order/@Priority/impl Ordered, then others by order of registration
    log.info("items: {}", items);
    assertThat(items)
        .map(Item::id)
        .isEqualTo(List.of("hp", "-10", "0", "10", "lp", "default"));
  }

}
