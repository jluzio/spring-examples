package com.example.spring.reactive.playground.hello

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*


@Component
class GreetingRouter {

  @Bean
  fun route(greetingHandler: GreetingHandler): RouterFunction<ServerResponse> =
      RouterFunctions.route(
          RequestPredicates.GET("/hello")
              .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
          HandlerFunction { request -> greetingHandler.hello(request) })
}