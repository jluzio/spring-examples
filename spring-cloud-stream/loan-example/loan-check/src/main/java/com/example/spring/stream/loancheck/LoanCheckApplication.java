package com.example.spring.stream.loancheck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
// TODO: replace with non-deprecated
@EnableBinding(LoanProcessor.class)
@Slf4j
public class LoanCheckApplication {

  public static void main(String[] args) {
    SpringApplication.run(LoanCheckApplication.class, args);
    log.info("The Loancheck Application has started...");
  }

}
