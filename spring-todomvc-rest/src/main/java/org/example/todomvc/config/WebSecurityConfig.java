package org.example.todomvc.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests()
//			.anyRequest().permitAll();
			
		http.authorizeRequests()
			.antMatchers("/", "/index.html", "/api/**").permitAll()
			.antMatchers("/protected/**").authenticated()
//			.anyRequest().authenticated()
			.and()
			.formLogin().loginPage("/login").permitAll()
		;
	}
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		super.configure(auth);
		auth.inMemoryAuthentication().withUser("user").password("pass").roles("USER");
//		auth.userDetailsService(userDetailsService());
	}

//	@Override
//	@Bean
//	protected UserDetailsService userDetailsService() {
//		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//		manager.createUser(
//				User.withDefaultPasswordEncoder().username("user").password("pass").roles("USER").build());
//		return manager;
//	}

}