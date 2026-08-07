package com.example.spring.cloud.playground.function;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.cloud.playground.function.model.User;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;

@FunctionalSpringBootTest
class LangFunctionsTest {

  @Autowired
  private FunctionCatalog catalog;

  @Test
  void uppercaseReactive() {
    Function<Flux<String>, Flux<String>> function = catalog.lookup(Function.class, "uppercaseReactive");
    assertThat(function.apply(Flux.just("Hello")).collectList().block())
        .isEqualTo(List.of("HELLO"));
  }

  @Test
  void lowercase() {
    Function<String, String> function = catalog.lookup(Function.class, "lowercase");
    assertThat(function.apply("Hello"))
        .isEqualTo("hello");
  }

  @Test
  void reverse() {
    Function<String, String> function = catalog.lookup(Function.class, "reverse");
    assertThat(function.apply("Hello"))
        .isEqualTo("olleH");
  }

  @Test
  void lowercase_reverse() {
    Function<String, String> function = catalog.lookup(Function.class, "lowercase,reverse");
    assertThat(function.apply("Hello"))
        .isEqualTo("olleh");
  }

  @Test
  void users() {
    Supplier<Flux<User>> function = catalog.lookup(Function.class, "users");
    assertThat(function.get().collectList().block())
        .hasSize(10);
  }
}
