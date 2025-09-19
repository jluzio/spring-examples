package com.example.spring.core.beans.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@SpringBootTest
@Log4j2
class ObjectProviderTest {

  record Foo(String id) {

  }

  record Bar(String id) {

  }

  @Configuration
  static class Config {

    @Bean
    @Primary
    Foo foo1() {
      return new Foo("foo1");
    }

    @Bean
    Foo foo2() {
      return new Foo("foo2");
    }

    @Bean
    @Qualifier("test")
    Foo foo3() {
      return new Foo("foo3");
    }
  }

  @Autowired
  ObjectProvider<Foo> fooObjectProvider;
  @Autowired
  @Qualifier("test")
  ObjectProvider<Foo> testFooObjectProvider;
  @Autowired
  @Qualifier("foo2")
  ObjectProvider<Foo> foo2ObjectProvider;

  @Test
  void test_basic() {
    // ObjectProvider is lazy
    Foo mainFoo = fooObjectProvider.getObject();
    assertThat(mainFoo)
        .isEqualTo(new Foo("foo1"));

    // same as getObject
    assertThat(fooObjectProvider.getIfUnique())
        .isEqualTo(mainFoo);

    assertThat(fooObjectProvider.getIfAvailable())
        .isNotNull();

    for (var foo : fooObjectProvider) {
      log.debug("foo: {}", foo);
    }

    List<String> fooIds = fooObjectProvider.stream()
        .map(Foo::id)
        .toList();
    assertThat(fooIds)
        .containsOnly("foo1", "foo2", "foo3");
  }

  @Test
  void test_qualifier() {
    assertThat(testFooObjectProvider.getIfUnique())
        .isEqualTo(new Foo("foo3"));
    assertThat(foo2ObjectProvider.getIfUnique())
        .isEqualTo(new Foo("foo2"));
  }

}
