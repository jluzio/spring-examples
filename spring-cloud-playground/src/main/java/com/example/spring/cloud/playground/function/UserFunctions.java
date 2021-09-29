package com.example.spring.cloud.playground.function;

import com.example.spring.cloud.playground.function.model.User;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class UserFunctions {

  private final UserService service;

  @Bean
  public Supplier<Flux<User>> users() {
    return service::getUsers;
  }

  @Bean
  public Function<User, String> username() {
    return User::getUsername;
  }

  @Bean
  public Function<User, String> email() {
    return User::getEmail;
  }

  @Bean
  public Function<Flux<String>, Flux<User>> findUser() {
    return usernames ->
        usernames.flatMap(username -> service.getUsers().filter(u -> username.equals(u.getUsername())));
  }

}
