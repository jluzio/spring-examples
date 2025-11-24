package com.example.spring.framework.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  public HelloController() {
    IO.println("hello");
  }

  @GetMapping(value = "/hello")
  public String helloBody() {
    return "Hello World!";
  }
}
