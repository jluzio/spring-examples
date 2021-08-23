package com.example.factory.dependency.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DependencyService {

  public void run(String context) {
    log.info("{}:run from {}", getClass().getName(), context);
  }

}
