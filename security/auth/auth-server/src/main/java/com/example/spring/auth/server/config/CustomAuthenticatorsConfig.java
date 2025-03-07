package com.example.spring.auth.server.config;

import com.example.spring.auth.server.customcode.CustomCodeGrantAuthenticationConverter;
import com.example.spring.auth.server.customcode.CustomCodeGrantAuthenticationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

@ConditionalOnProperty(value = "app.authenticators.custom.enabled", havingValue = "true")
public class CustomAuthenticatorsConfig {

  @Bean
  CustomCodeGrantAuthenticationConverter customCodeGrantAuthenticationConverter() {
    return new CustomCodeGrantAuthenticationConverter();
  }

  @Bean
  CustomCodeGrantAuthenticationProvider customCodeGrantAuthenticationProvider(
      OAuth2AuthorizationService authorizationService,OAuth2TokenGenerator<?> tokenGenerator) {
    return new CustomCodeGrantAuthenticationProvider(
        authorizationService, tokenGenerator);
  }

}