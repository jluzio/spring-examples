package com.example.spring.framework.api.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

  @Bean
  public RouterFunction<ServerResponse> userRouterFunctions(UserHandler userHandler) {
    return RouterFunctions.route(
            RequestPredicates.GET("/webflux/users/{id}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
            userHandler::user)
        .and(
            RouterFunctions.route(
                RequestPredicates.GET("/webflux/users")
                    .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                userHandler::users)
        );
  }
}