package com.example.spring.framework.beans;

import static org.assertj.core.api.Assertions.assertThat;

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

@SpringBootTest
@Slf4j
class OrderUsingCustomOrderTest {

  record Item(String id) {

  }

  @Configuration
  static class Config {

    @Bean
    @Order
    Item item1() {
      return new Item("1");
    }

    @Bean
    Item item2() {
      return new Item("2");
    }

    // NOTE: to replace some reference to all beans of this time, it should have the same name from where they are referenced
    @Bean
    List<Item> customOrderItems(List<Item> items) {
      return items.reversed();
    }
  }

  @Autowired
  List<Item> items;
  @Autowired
  List<Item> customOrderItems;
  @Autowired
  ApplicationContext context;

  @Test
  void test() {
    log.info("beans: {}", Arrays.stream(context.getBeanNamesForType(Item.class)).toList());

    log.info("items: {}", items);
    assertThat(items)
        .map(Item::id)
        .isEqualTo(List.of("1", "2"));

    log.info("customOrderItems: {}", customOrderItems);
    assertThat(customOrderItems)
        .map(Item::id)
        .isEqualTo(List.of("2", "1"));
  }

}
