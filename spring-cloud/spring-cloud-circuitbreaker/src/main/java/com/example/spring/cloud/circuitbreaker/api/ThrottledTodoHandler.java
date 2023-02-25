package com.example.spring.cloud.circuitbreaker.api;

import com.example.spring.cloud.circuitbreaker.model.Todo;
import com.example.spring.cloud.circuitbreaker.service.TodoService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ThrottledTodoHandler {

  private final TodoService todoService;

  public Mono<ServerResponse> todos(ServerRequest request) {
    return delay(request)
        .flatMap(ignored -> ServerResponse.ok()
            .body(BodyInserters.fromPublisher(todoService.getTodos(), Todo.class)));
  }

  public Mono<ServerResponse> todo(ServerRequest request) {
    String id = request.pathVariable("id");
    return delay(request)
        .flatMap(ignored -> ServerResponse.ok()
            .body(BodyInserters.fromPublisher(todoService.getTodo(id), Todo.class)));
  }

  public Mono<ServerResponse> addTodo(ServerRequest request) {
    return delay(request)
        .flatMap(ignored -> request.bodyToMono(Todo.class))
        .flatMap(todo -> ServerResponse.ok()
            .body(BodyInserters.fromPublisher(todoService.addTodo(todo), Todo.class)));
  }

  private Mono<Long> delay(ServerRequest request) {
    Duration delay = request.queryParam("delay")
        .map(Duration::parse)
        .orElse(Duration.ofMillis(0));
    return Mono.delay(delay);
  }

}
