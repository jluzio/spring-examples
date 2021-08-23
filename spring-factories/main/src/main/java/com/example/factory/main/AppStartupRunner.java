package com.example.factory.main;

import com.example.factory.dependency.service.DependencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppStartupRunner implements ApplicationRunner {
  @Autowired
  DependencyService service;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    service.run("Main");
  }
}
