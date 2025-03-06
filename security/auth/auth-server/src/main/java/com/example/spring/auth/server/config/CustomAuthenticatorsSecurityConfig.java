package com.example.spring.auth.server.config;

import static com.example.spring.auth.server.customcode.CustomCodeGrantAuthenticationToken.CUSTOM_CODE_GRANT_TYPE;

import com.example.spring.auth.server.customcode.CustomCodeGrantAuthenticationConverter;
import com.example.spring.auth.server.customcode.CustomCodeGrantAuthenticationProvider;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(value = "app.security-config", havingValue = "custom-authenticators")
public class CustomAuthenticatorsSecurityConfig {

  @Bean
  SecurityFilterChain authorizationServerSecurityFilterChain(
      HttpSecurity http,
      OAuth2AuthorizationService authorizationService,
      OAuth2TokenGenerator<?> tokenGenerator
  ) throws Exception {

    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        OAuth2AuthorizationServerConfigurer.authorizationServer();

    http
        .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
        .with(authorizationServerConfigurer, authorizationServer ->
            authorizationServer
                .tokenEndpoint(tokenEndpoint ->
                    tokenEndpoint
                        .accessTokenRequestConverter(
                            new CustomCodeGrantAuthenticationConverter())
                        .authenticationProvider(
                            new CustomCodeGrantAuthenticationProvider(
                                authorizationService, tokenGenerator)))
        )
        .authorizeHttpRequests(authorize ->
            authorize
                .anyRequest().authenticated()
        );

    return http.build();
  }

  // Using client from properties, otherwise it would require this config
  // @Bean
  RegisteredClientRepository registeredClientRepository() {
    RegisteredClient messagingClient = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("custom-code-client")
        .clientSecret("{noop}secret-custom-code-cli")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
        .authorizationGrantType(new AuthorizationGrantType(CUSTOM_CODE_GRANT_TYPE))
        .scope("message.read")
        .scope("message.write")
        .build();

    return new InMemoryRegisteredClientRepository(messagingClient);
  }

  @Bean
  OAuth2AuthorizationService authorizationService() {
    return new InMemoryOAuth2AuthorizationService();
  }

  @Bean
  OAuth2TokenGenerator<?> tokenGenerator(JWKSource<SecurityContext> jwkSource) {
    JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
  }

}