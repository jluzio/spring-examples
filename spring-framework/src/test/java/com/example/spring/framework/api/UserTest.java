package com.example.spring.framework.api;

import com.example.spring.framework.api.service.UserService;
import com.example.spring.framework.api.webmvc.UserController;
import com.example.types.User;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(UserController.class)
@AutoConfigureRestTestClient
@Import({UserService.class})
@Slf4j
class UserTest {

  static final ParameterizedTypeReference<ArrayList<User>> USER_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
  };

  @Autowired
  RestTestClient restTestClient;
  @Autowired
  UserService userService;

  @Test
  void users() {
    var publicUsers = userService.getUsers().stream()
        .map(this::publicUser)
        .toList();

    restTestClient
        // Create a GET request to test an endpoint
        .get().uri("/users")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(USER_LIST_TYPE_REF)
        .isEqualTo(new ArrayList<>(publicUsers));
  }

  @Test
  void user() {
    var user1 = userService.findUser("1").orElseThrow();

    restTestClient
        // Create a GET request to test an endpoint
        .get().uri("/users/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(User.class).isEqualTo(user1);
  }

  @Test
  void user_not_found() {
    restTestClient
        // Create a GET request to test an endpoint
        .get().uri("/users/doesntexist")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isNotFound();
  }

  private User publicUser(User user) {
    return new User()
        .withId(user.getId())
        .withUsername(user.getUsername());
  }
}
