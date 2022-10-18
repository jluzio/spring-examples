package com.example.spring.core.api.webmvc;

import com.example.spring.core.api.model.view.Public;
import com.example.spring.core.api.service.UserService;
import com.example.types.User;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

  @GetMapping(path = "/webmvc/users")
  @JsonView(Public.class)
  public List<User> getUsers() {
    return service.getUsers().collectList().block();
  }

  @GetMapping(path = "/webmvc/users/{id}")
  public User findUser(@PathVariable("id") String id) {
    return service.findUser(id).blockOptional()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

}
