package com.example.spring.auth.client.config;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions;
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

    return route("app")
        .GET("/app/**", http("http://localhost:8081"))
        .filter(TokenRelayFilterFunctions.tokenRelay())
        .filter(FilterFunctions.rewritePath("/app/(?<segment>.*)", "/${segment}"))
        .build();
  }

}
