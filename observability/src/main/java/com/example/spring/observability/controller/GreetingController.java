package com.example.spring.observability.controller;

import static java.util.Optional.ofNullable;

import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greetings")
@Slf4j
public class GreetingController {

  @GetMapping("/{name}")
  @Observed(name = "name", contextualName = "greetingByName")
  public String greeting(@PathVariable(required = false) String name) {
    log.info("greeting: name={}", name);
    return "Hello, %s!".formatted(
        ofNullable(name).orElse("world"));
  }

}
