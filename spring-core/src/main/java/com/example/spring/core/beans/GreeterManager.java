package com.example.spring.core.beans;

import lombok.Data;

@Data
public class GreeterManager {

  private Greeter greeter;
  private Greeter fancyGreeter;

  public void greet() {
    greeter.sayHello();
  }

  public void fancyGreet() {
    fancyGreeter.sayHello();
  }

}
