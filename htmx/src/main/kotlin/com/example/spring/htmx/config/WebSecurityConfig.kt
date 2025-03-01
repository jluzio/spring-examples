package com.example.spring.htmx.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class WebSecurityConfig {

  @Bean
  @Throws(Exception::class)
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .authorizeHttpRequests { requests ->
        requests
          .requestMatchers("/**").permitAll()
          .anyRequest().authenticated()
      }
//      .formLogin { form ->
//        form
//          .loginPage("/login")
//          .permitAll()
//      }
      .logout { logout -> logout.permitAll() }
    return http.build()
  }

/*
  @Bean
  fun userDetailsService(): UserDetailsService {
    val user: UserDetails = User.withDefaultPasswordEncoder()
      .username("user")
      .password("password")
      .roles("USER")
      .build()
    return InMemoryUserDetailsManager(user)
  }
*/

}
