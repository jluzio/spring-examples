package com.example.spring.auth.client.config;

import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@Profile("oauth-provider-local")
public class RouteConfig {

  @Bean
  RouterFunction<ServerResponse> gatewayRouterFunctionsAddReqHeader() {
    return route()
        .GET("/app", http("http://localhost:8081"))
        .filter(tokenRelay())
        .build();
  }

}
