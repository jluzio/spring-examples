package com.example.spring.monitoring.controller;

import static java.util.Optional.ofNullable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greetings")
public class GreetingController {

  @GetMapping("/{name}")
  public String hello(@PathVariable(required = false) String name) {
    return "Hello, %s!".formatted(
        ofNullable(name).orElse("world"));
  }

}
