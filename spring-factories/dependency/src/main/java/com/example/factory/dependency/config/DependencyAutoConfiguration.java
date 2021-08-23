package com.example.factory.dependency.config;

import com.example.factory.dependency.service.DependencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Slf4j
@Import({DependencyService.class})
public class DependencyAutoConfiguration {

  public DependencyAutoConfiguration() {
    log.info("DependencyAutoConfiguration<init>");
  }
}
