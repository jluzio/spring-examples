package com.example.spring.core;

import com.example.spring.core.beans.Greeter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("standalone")
public class AppCommandLineRunner implements CommandLineRunner {
  @Autowired
  @Qualifier("normalGreeter")
  private Greeter greeter;

  @Override
  public void run(String... args) throws Exception {
    greeter.sayHello();
  }

}
