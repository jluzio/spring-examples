package com.example.spring.framework.api.webmvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

  public record Message(String message, String target) {

  }

  @GetMapping(path = "/webmvc/hello")
  public String hello() {
    return "Hello, WebMvc Spring!";
  }

  @GetMapping(path = "/webmvc/hello-message")
  public Message helloMessage() {
    return new Message("Hello, WebMvc Spring!", "WebMvc Spring");
  }

}
