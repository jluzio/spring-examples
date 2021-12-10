package com.example.spring.core.beans;

import com.example.spring.core.qualifier.FooQualifier;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;

@SpringBootTest
@Slf4j
public class AnnotationsTest {

  @Autowired
  List<Item> items;

  @Autowired
  Optional<OptionalItem> optionalItem1;
  @Autowired(required = false)
  OptionalItem optionalItem2;
  @Autowired
  @Nullable
  OptionalItem optionalItem3;

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

  @Value("${logging.level.com.example.spring:value-if-not-exists}")
  String someProp;
  @Value("${test.hello:value-if-not-exists}")
  String stringProp;
  @Value("${test.csv}")
  List<Integer> csvProp;
  @Value("${test.csv}")
  List<String> strCsvProp;
  @Value("#{'${test.hello} world!'}")
  String stringPropEl;

  @Test
  void test() {
    // gives first beans with @Order/@Priority/impl Ordered, then others by order of registration
    log.info("items: {}", items);

    log.info("optionalItems: {}", Lists.newArrayList(optionalItem1, optionalItem2, optionalItem3));

    log.info("primaryItem: {}", primaryItem);

    log.info("secondItem: {}", secondItem);

    log.info("fooBarItem: {}", fooBarItem);

    // Generics as implicit qualifier
    log.info("intListBean: {}", intListBean);
    log.info("strListBean: {}", strListBean);

    log.info("resourceItem: {}", resourceItem);
    log.info("awResourceItem: {}", awResourceItem);

    log.info("someProp: {}", someProp);
    log.info("stringProp: {}", stringProp);
    log.info("csvProp: {}", csvProp);
    log.info("strCsvProp: {}", strCsvProp);
    log.info("stringPropEl: {}", stringPropEl);
  }

  @TestConfiguration
  static class Config {

    @Bean
    @Qualifier("second")
    @Order(2)
    Item item2() {
      return Item.builder()
          .id("2")
          .build();
    }

    @Bean
    @Order(1)
    Item item1() {
      return Item.builder()
          .id("1")
          .build();
    }

    @Bean
    @Order(3)
    @Primary
    Item item3() {
      return Item.builder()
          .id("3")
          .build();
    }

    @Bean
    @FooQualifier("bar")
    Item item5() {
      return Item.builder()
          .id("5")
          .build();
    }

    @Bean
    @FooQualifier("barz")
    Item item4() {
      return Item.builder()
          .id("4")
          .build();
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
      return Item.builder()
          .id("resource-item")
          .build();
    }
  }

  @Data
  @Builder
  static class Item {

    private String id;
  }

  @Data
  @Builder
  static class OptionalItem {

    private String id;
  }

}
