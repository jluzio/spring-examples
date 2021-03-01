package com.example.spring.core.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("theFancyGreeter")
@Slf4j
public class FancyGreeter implements Greeter {

  @Override
  public void sayHello() {
    log.info("Hello sir!");
  }

}
