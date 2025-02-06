package com.example.spring.webstack.webflux.api;

import com.example.spring.webstack.webflux.model.User;
import com.example.spring.webstack.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

  private final UserService service;


  public Mono<ServerResponse> users(ServerRequest request) {
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.getUsers(), User.class);
  }

  public Mono<ServerResponse> user(ServerRequest request) {
    String id = request.pathVariable("id");
    return service.findUser(id)
        .flatMap(user -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(user))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

}