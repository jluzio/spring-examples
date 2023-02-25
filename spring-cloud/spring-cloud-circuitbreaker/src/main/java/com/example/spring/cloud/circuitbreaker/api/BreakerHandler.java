package com.example.spring.cloud.circuitbreaker.api;

import com.example.spring.cloud.circuitbreaker.model.Todo;
import com.example.spring.cloud.circuitbreaker.service.BreakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BreakerHandler {

  private final BreakerService breakerService;
  private final WebClient localWebClient;


  public Mono<ServerResponse> slowApiTodos(ServerRequest request) {
    String delayedTodosUri = uriWithDelayQueryParam("/todos?delay=%s", request);
    Todo[] fallbackTodos = {Todo.builder().name("fallback").build()};
    return localWebClient.get()
        .uri(delayedTodosUri)
        .retrieve()
        .bodyToMono(Todo[].class)
        .transform(it -> breakerService.slowApi(it, fallbackTodos))
        .flatMap(ServerResponse.ok()::bodyValue);
  }

  public Mono<ServerResponse> defaultApiTodos(ServerRequest request) {
    String delayedTodosUri = uriWithDelayQueryParam("/todos?delay=%s", request);
    Todo[] fallbackTodos = {Todo.builder().name("fallback").build()};
    return localWebClient.get()
        .uri(delayedTodosUri)
        .retrieve()
        .bodyToMono(Todo[].class)
        .transform(it -> breakerService.defaultApi(it, fallbackTodos))
        .flatMap(ServerResponse.ok()::bodyValue);
  }

  private String uriWithDelayQueryParam(String uriTemplate, ServerRequest serverRequest) {
    var delay = serverRequest.queryParam("delay")
        .orElse("PT5S");
    return uriTemplate.formatted(delay);

  }
}
