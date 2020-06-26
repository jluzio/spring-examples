package com.example.spring.core.beans;

public class GreeterManager {
  private Greeter greeter;
  private Greeter fancyGreeter;

  public void greet() {
    greeter.sayHello();
  }

  public void fancyGreet() {
    fancyGreeter.sayHello();
  }

  public Greeter getGreeter() {
    return greeter;
  }

  public void setGreeter(Greeter greeter) {
    this.greeter = greeter;
  }

  public Greeter getFancyGreeter() {
    return fancyGreeter;
  }

  public void setFancyGreeter(Greeter fancyGreeter) {
    this.fancyGreeter = fancyGreeter;
  }

}
