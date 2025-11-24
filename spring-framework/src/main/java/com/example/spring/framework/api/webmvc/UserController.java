package com.example.spring.framework.api.webmvc;

import com.example.spring.framework.api.model.view.Public;
import com.example.spring.framework.api.service.UserService;
import com.example.types.User;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

  @GetMapping(path = "/users")
  @JsonView(Public.class)
  public List<User> getUsers() {
    return service.getUsers();
  }

  @GetMapping(path = "/users/{id}")
  public User findUser(@PathVariable("id") String id) {
    return service.findUser(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

}
