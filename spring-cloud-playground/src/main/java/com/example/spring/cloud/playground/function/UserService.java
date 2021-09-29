package com.example.spring.cloud.playground.function;

import com.example.spring.cloud.playground.function.model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class UserService {

  private final Flux<User> users = Flux.range(1, 10)
      .map(id -> new User("user-%s".formatted(id), "user-%s@mail.org".formatted(id)));

  public Flux<User> getUsers() {
    return users;
  }
}
