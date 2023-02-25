package com.example.spring.cloud.circuitbreaker.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BreakerRouter {

  @Bean
  RouterFunction<ServerResponse> slowTodos(BreakerHandler breakerHandler) {
    return RouterFunctions.route(
        RequestPredicates.GET("/circuit-breaker/slow/todos"),
        breakerHandler::slowApiTodos
    );
  }

  @Bean
  RouterFunction<ServerResponse> defaultodos(BreakerHandler breakerHandler) {
    return RouterFunctions.route(
        RequestPredicates.GET("/circuit-breaker/default/todos"),
        breakerHandler::defaultApiTodos
    );
  }

}
