package com.example.spring.core.api.webflux;

import com.example.spring.core.api.model.view.Detailed;
import com.example.spring.core.api.model.view.Public;
import com.example.spring.core.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2CodecSupport;
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
        .hint(Jackson2CodecSupport.JSON_VIEW_HINT, Public.class)
        .bodyValue(service.getUsers().collectList().block());
  }

  public Mono<ServerResponse> user(ServerRequest request) {
    String id = request.pathVariable("id");
    return service.findUser(id)
        .map(user -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .hint(Jackson2CodecSupport.JSON_VIEW_HINT, Detailed.class)
            .bodyValue(user))
        .defaultIfEmpty(ServerResponse.notFound().build())
        .block();
  }

}