package com.example.spring.framework.api;

import com.example.spring.framework.api.service.UserService;
import com.example.types.User;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = {
    com.example.spring.framework.api.webflux.UserRouter.class,
    com.example.spring.framework.api.webflux.UserHandler.class,
    com.example.spring.framework.api.webmvc.UserController.class
})
@Import({UserService.class})
@Slf4j
class UserTest {

  @Autowired
  WebTestClient webTestClient;
  @Autowired
  UserService userService;

  static final ParameterizedTypeReference<ArrayList<User>> USER_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
  };

  @Test
  void users() {
    var publicUsers = userService.getUsers()
        .map(this::publicUser)
        .toStream()
        .toList();

    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webflux/users")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(USER_LIST_TYPE_REF).isEqualTo(new ArrayList<>(publicUsers));
  }

  @Test
  void user() {
    var user1 = userService.findUser("1")
        .blockOptional()
        .orElseThrow();

    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webflux/users/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(User.class).isEqualTo(user1);
  }

  @Test
  void user_not_found() {
    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webflux/users/doesntexist")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isNotFound();
  }

  @Test
  void mvc_users() {
    var publicUsers = userService.getUsers()
        .map(this::publicUser)
        .toStream()
        .toList();

    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webmvc/users")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(USER_LIST_TYPE_REF).isEqualTo(new ArrayList<>(publicUsers));
  }


  @Test
  void mvc_user() {
    var user1 = userService.findUser("1")
        .blockOptional()
        .orElseThrow();

    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webmvc/users/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        // and use the dedicated DSL to test assertions against the response
        .expectStatus().isOk()
        .expectBody(User.class).isEqualTo(user1);
  }

  @Test
  void mvc_user_not_found() {
    webTestClient
        // Create a GET request to test an endpoint
        .get().uri("/webmvc/users/doesntexist")
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
