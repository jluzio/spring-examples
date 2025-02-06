package com.example.spring.webstack.web.api;

import com.example.spring.webstack.web.model.User;
import com.example.spring.webstack.web.service.UserService;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

  @GetMapping(path = "/users")
  public Stream<User> getUsers() {
    return service.getUsers();
  }

  @GetMapping(path = "/users/{id}")
  public User findUser(@PathVariable("id") String id) {
    return service.findUser(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

}
