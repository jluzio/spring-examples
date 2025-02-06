package com.example.spring.webstack.webflux.service;

import com.example.spring.webstack.webflux.model.User;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserService {

  private final List<User> users = List.of(
      user("1", "john.doe"),
      user("2", "jane.doe"),
      user("3", "tom.doe")
  );

  public Flux<User> getUsers() {
    return Flux.fromIterable(users);
  }

  public Mono<User> findUser(String id) {
    var maybeUser = users.stream()
        .filter(u -> Objects.equals(u.id(), id))
        .findFirst();
    return Mono.justOrEmpty(maybeUser);
  }

  private User user(String id, String username) {
    return User
        .builder()
        .id(id)
        .username(username)
        .email(username + "@mail.org")
        .fullName(StringUtils.capitalize(username.replace(',', ' ')))
        .build();
  }

}