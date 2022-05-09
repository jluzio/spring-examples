package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = {
    ConfigurationOrderTest.Config2.class,
    ConfigurationOrderTest.Config1.class})
@Slf4j
class ConfigurationOrderTest {

  record Item(String id) {

  }

  @Configuration
  static class Config1 {

    @Bean
    Item item_cfg1_1() {
      return new Item("cfg1_1");
    }

    @Bean
    Item item_cfg1_2() {
      return new Item("cfg1_2");
    }
  }

  @Configuration
  static class Config2 {

    @Bean
    Item item_cfg2_1() {
      return new Item("cfg2_1");
    }

    @Bean
    Item item_cfg2_2() {
      return new Item("cfg2_2");
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
        .isEqualTo(List.of("cfg2_1", "cfg2_2", "cfg1_1", "cfg1_2"));
  }

}
