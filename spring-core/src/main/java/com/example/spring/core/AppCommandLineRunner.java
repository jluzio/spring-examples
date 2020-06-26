package com.example.spring.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.spring.core.beans.Greeter;

@Component
public class AppCommandLineRunner implements CommandLineRunner {
  @Autowired
  @Qualifier("normalGreeter")
  private Greeter greeter;

  @Override
  public void run(String... args) throws Exception {
    greeter.sayHello();
  }

}
