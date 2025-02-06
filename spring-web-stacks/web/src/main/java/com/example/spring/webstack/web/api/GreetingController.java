package com.example.spring.webstack.web.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

  public record Message(String message, String target) {

  }

  @GetMapping(path = "/hello")
  public String hello() {
    return "Hello, WebMvc Spring!";
  }

  @GetMapping(path = "/hello-message")
  public Message helloMessage() {
    return new Message("Hello, WebMvc Spring!", "WebMvc Spring");
  }

}
