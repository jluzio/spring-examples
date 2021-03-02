package com.example.spring.core.api.webmvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

  @GetMapping(path = "/webmvc/hello")
  public String hello() {
    return "Hello, WebMvc Spring!";
  }

}
