package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.core.qualifier.FooQualifier;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

@SpringBootTest(classes = QualifierTest.Config.class)
@Slf4j
class QualifierTest {

  @Configuration
  static class Config {

    @Bean
    @Qualifier("second")
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
    @Primary
    Item item3() {
      return new Item("3");
    }

    @Bean
    @FooQualifier("bar")
    Item item5() {
      return new Item("5");
    }

    @Bean
    @FooQualifier("barz")
    Item item4() {
      return new Item("4");
    }

    // to avoid clashing with other Integer or String beans
    @Bean
    @Qualifier("list")
    List<Integer> intList() {
      return List.of(1, 2, 3);
    }

    // to avoid clashing with other Integer or String beans (e.g. appVersion)
    @Bean
    @Qualifier("list")
    List<String> strList() {
      return List.of("a", "b", "c");
    }

    @Bean
      // bean name = "resourceItem", which is used in injection
    Item resourceItem() {
      return new Item("resource-item");
    }
  }

  @Data
  @Builder
  static class Item {

    private String id;
  }


  @Autowired
  Item primaryItem;

  @Autowired
  @Qualifier("second")
  Item secondItem;

  @Autowired
  @FooQualifier("bar")
  Item fooBarItem;

  // to avoid clashing with other Integer or String beans
  @Autowired
  @Qualifier("list")
  List<Integer> intListBean;

  // to avoid clashing with other Integer or String beans
  @Autowired
  @Qualifier("list")
  List<String> strListBean;

  // Equivalent to @Autowired @Qualifier("resourceItem")
  @Resource
  Item resourceItem;

  @Autowired
  @Qualifier("resourceItem")
  Item awResourceItem;


  @Test
  void test() {
    log.info("primaryItem: {}", primaryItem);
    assertThat(primaryItem.getId())
        .isEqualTo("3");

    log.info("secondItem: {}", secondItem);
    assertThat(secondItem.getId())
        .isEqualTo("2");

    log.info("fooBarItem: {}", fooBarItem);
    assertThat(fooBarItem.getId())
        .isEqualTo("5");

    // Generics as implicit qualifier
    log.info("intListBean: {}", intListBean);
    log.info("strListBean: {}", strListBean);

    log.info("resourceItem: {}", resourceItem);
    log.info("awResourceItem: {}", awResourceItem);
    assertThat(resourceItem)
        .isEqualTo(awResourceItem)
        .hasFieldOrPropertyWithValue("id", "resource-item");
  }
}
