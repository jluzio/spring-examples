package com.example.spring.boot.playground.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloResource {

	public record Greeting(String name) {}
	
	@GetMapping("/hello")
	public String hello() {
		return "Hello World!";
	}

	@PostMapping("/hello")
	public String postHello(@RequestBody Greeting greeting) {
		return "Hello %s!".formatted(greeting.name());
	}

}
