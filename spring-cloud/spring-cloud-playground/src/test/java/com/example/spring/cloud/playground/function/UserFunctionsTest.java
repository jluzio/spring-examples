package com.example.spring.cloud.playground.function;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.cloud.playground.function.model.User;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;

@FunctionalSpringBootTest
class UserFunctionsTest {

  @Autowired
  private FunctionCatalog catalog;
  @Autowired
  private UserService service;


  @Test
  void users() throws Exception {
    Supplier<Flux<User>> function = catalog.lookup("users");
    assertThat(function.get().collectList().block())
        .isEqualTo(service.getUsers().collectList().block());
  }

  @Test
  void users_username() throws Exception {
    Supplier<Flux<String>> function = catalog.lookup("users,username");
    assertThat(function.get().collectList().block())
        .isEqualTo(service.getUsers()
            .map(User::getUsername)
            .collectList()
            .block());
  }

  @Test
  void users_username_reverse() throws Exception {
    Supplier<Flux<String>> function = catalog.lookup("users,username,reverse");
    assertThat(function.get().collectList().block())
        .isEqualTo(service.getUsers()
            .map(User::getUsername)
            .map(StringUtils::reverse)
            .collectList()
            .block());
  }

  @Test
  void findUser_email() throws Exception {
    Function<Flux<String>, Flux<User>> function = catalog.lookup("findUser,email");
    var usernames = List.of("user-1", "user-3");
    assertThat(function.apply(Flux.fromIterable(usernames)).collectList().block())
        .isEqualTo(service.getUsers()
            .filter(u -> usernames.contains(u.getUsername()))
            .map(User::getEmail)
            .collectList()
            .block());
  }
}
