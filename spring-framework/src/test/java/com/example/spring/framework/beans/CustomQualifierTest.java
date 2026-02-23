package com.example.spring.framework.beans;


import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Slf4j
class CustomQualifierTest {

  @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @Qualifier
  public @interface FooA {

  }

  @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @Qualifier
  public @interface FooB {

  }

  @Configuration
  static class Config {

    @Bean
    @FooA
    Item item1() {
      return new Item("item1");
    }

    @Bean
    @FooB
    Item item2() {
      return new Item("item2");
    }

    @Bean
    @FooA
    @FooB
    Item item3() {
      return new Item("item3");
    }

  }

  @Builder
  record Item(String id) {

  }


  @Autowired
  @FooA
  List<Item> fooAItems;
  @Autowired
  @FooB
  List<Item> fooBItems;

  @Test
  void test() {
    assertThat(ids(fooAItems))
        .containsExactly("item1", "item3");
    assertThat(ids(fooBItems))
        .containsExactly("item2", "item3");
  }

  private List<String> ids(Collection<Item> list) {
    return list.stream()
        .map(Item::id)
        .toList();
  }
}
