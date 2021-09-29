package com.example.spring.cloud.playground.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FunctionalSpringBootTest
class NonHttpWebFunctionsTest {

  @Autowired
  private FunctionCatalog catalog;

  @Test
  void uppercase() throws Exception {
    Function<Flux<String>, Flux<String>> function = catalog.lookup(Function.class, "uppercase");
    assertThat(function.apply(Flux.just("Hello")).collectList().block())
        .isEqualTo(List.of("HELLO"));
  }

  @Test
  void lowercase() throws Exception {
    Function<String, String> function = catalog.lookup(Function.class, "lowercase");
    assertThat(function.apply("Hello"))
        .isEqualTo("hello");
  }

  @Test
  void reverse() throws Exception {
    Function<String, String> function = catalog.lookup(Function.class, "lowercase");
    assertThat(function.apply("Hello"))
        .isEqualTo("olleH");
  }

  @Test
  void lowercase_reverse() throws Exception {
    Function<String, String> function = catalog.lookup(Function.class, "lowercase,reverse");
    assertThat(function.apply("Hello"))
        .isEqualTo("olleh");
  }
}
