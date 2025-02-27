package com.example.spring.auth.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  @Profile("!oauth-provider-local")
  SecurityFilterChain securityFilterChainDefault(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers("/").permitAll();
          auth.anyRequest().authenticated();
        })
        .oauth2Login(Customizer.withDefaults())
        .formLogin(Customizer.withDefaults())
        .build();
  }

  @Bean
  @Profile("oauth-provider-local")
  SecurityFilterChain securityFilterChainOAuthProviderLocal(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> {
//          auth.requestMatchers("/").permitAll();
          auth.anyRequest().authenticated();
        })
        .oauth2Login(Customizer.withDefaults())
        .oauth2Client(Customizer.withDefaults())
//        .formLogin(withDefaults())
        .build();
  }

}
