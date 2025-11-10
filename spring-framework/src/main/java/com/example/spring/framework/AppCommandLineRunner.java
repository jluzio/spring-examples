package com.example.spring.framework;

import com.example.spring.framework.beans.Greeter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("standalone")
public class AppCommandLineRunner implements CommandLineRunner {

  private Greeter greeter;

  public AppCommandLineRunner(@Qualifier("normalGreeter") Greeter greeter) {
    this.greeter = greeter;
  }

  @Override
  public void run(String... args) throws Exception {
    greeter.sayHello();
  }

}
