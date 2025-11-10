package com.example.spring.framework.api.service;

import com.example.types.User;
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
        .filter(u -> Objects.equals(u.getId(), id))
        .findFirst();
    return Mono.justOrEmpty(maybeUser);
  }

  private User user(String id, String username) {
    return new User()
        .withId(id)
        .withUsername(username)
        .withEmail(username + "@mail.org")
        .withFullName(StringUtils.capitalize(username.replace(',', ' ')));
  }

}