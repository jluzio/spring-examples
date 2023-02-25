package com.example.spring.cloud.circuitbreaker.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TodoRouter {

  @Bean
  RouterFunction<ServerResponse> todos(ThrottledTodoHandler todoHandler) {
    return RouterFunctions.route(
        RequestPredicates.GET("/todos"),
        todoHandler::todos
    );
  }

  @Bean
  RouterFunction<ServerResponse> todo(ThrottledTodoHandler todoHandler) {
    return RouterFunctions.route(
        RequestPredicates.GET("/todos/{id}"),
        todoHandler::todo
    );
  }

  @Bean
  RouterFunction<ServerResponse> addTodo(ThrottledTodoHandler todoHandler) {
    return RouterFunctions.route(
        RequestPredicates.POST("/todos"),
        todoHandler::addTodo
    );
  }

}
