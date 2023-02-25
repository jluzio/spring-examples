package com.example.spring.cloud.circuitbreaker.service;

import com.example.spring.cloud.circuitbreaker.model.Todo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TodoService {
  private final TodoConfig todoConfig;
  private final List<Todo> todos;

  public TodoService(TodoConfig todoConfig) {
    this.todoConfig = todoConfig;
    this.todos = new ArrayList<>(todoConfig.getTodos());
  }

  public Flux<Todo> getTodos() {
    return Flux.fromIterable(todos);
  }

  public Mono<Todo> getTodo(String id) {
    return Flux.fromIterable(todos)
        .filter(todo -> todo.id().equals(id))
        .next();
  }

  public Mono<Todo> addTodo(Todo todo) {
    Todo addedTodo = todo.toBuilder()
        .id(UUID.randomUUID().toString())
        .build();
    todos.add(addedTodo);
    return Mono.just(addedTodo);
  }

}
