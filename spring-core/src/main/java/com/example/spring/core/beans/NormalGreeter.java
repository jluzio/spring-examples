package com.example.spring.core.beans;

import org.springframework.stereotype.Component;

@Component
public class NormalGreeter implements Greeter {

  @Override
  public void sayHello() {
    System.out.println("Hello!");
  }

}
