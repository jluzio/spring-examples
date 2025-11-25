package com.example.spring.framework.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestsHelloController {

  @GetMapping(value = "/tests/hello")
  public String helloBody() {
    return "Hello World!";
  }
}
