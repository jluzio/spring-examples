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
class ConfigurationOrderTest {

  record Item(String id) {

  }

  @Configuration
  @Order(2)
  static class Config1 {

    @Bean
    @Order(0)
    Item item_cfg1_1() {
      return new Item("cfg1_1");
    }

    @Bean
    Item item_cfg1_2() {
      return new Item("cfg1_2");
    }

    @Bean
    @Order
    Item item_cfg1_3() {
      return new Item("cfg1_3");
    }
  }

  @Configuration
  @Order(1)
  static class Config2 {

    @Bean
    @Order(1)
    Item item_cfg2_1() {
      return new Item("cfg2_1");
    }

    @Bean
    Item item_cfg2_2() {
      return new Item("cfg2_2");
    }

    @Bean
    @Order
    Item item_cfg2_3() {
      return new Item("cfg2_3");
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
        .containsExactly("cfg1_1", "cfg2_1", "cfg2_2", "cfg2_3", "cfg1_2", "cfg1_3");
  }

}
