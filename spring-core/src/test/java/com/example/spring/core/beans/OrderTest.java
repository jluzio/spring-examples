package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@SpringBootTest
@Slf4j
class OrderTest {

  record Item(String id) {

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
    @Order(3)
    Item item3() {
      return new Item("3");
    }

    @Bean
    Item item5() {
      return new Item("5");
    }

    @Bean
    Item item4() {
      return new Item("4");
    }

    @Bean
      // bean name = "resourceItem", which is used in injection
    Item resourceItem() {
      return new Item("resource-item");
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
        .isEqualTo(List.of("1", "2", "3", "5", "4", "resource-item"));
  }

}
