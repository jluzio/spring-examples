package com.example.spring.webstack.web.service;

import com.example.spring.webstack.web.model.User;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserService {

  private final List<User> users = List.of(
      user("1", "john.doe"),
      user("2", "jane.doe"),
      user("3", "tom.doe")
  );

  public Stream<User> getUsers() {
    return users.stream();
  }

  public Optional<User> findUser(String id) {
    return users.stream()
        .filter(u -> Objects.equals(u.id(), id))
        .findFirst();
  }

  private User user(String id, String username) {
    return User.builder()
        .id(id)
        .username(username)
        .email(username + "@mail.org")
        .fullName(StringUtils.capitalize(username.replace(',', ' ')))
        .build();
  }

}