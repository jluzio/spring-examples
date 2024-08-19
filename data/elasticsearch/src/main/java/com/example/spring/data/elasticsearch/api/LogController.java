package com.example.spring.data.elasticsearch.api;

import static java.util.Optional.ofNullable;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
@Log4j2
public class LogController {

  @GetMapping("/hello")
  public String hello(@RequestParam(required = false) String who) {
    var msg = "Hello, %s".formatted(ofNullable(who).orElse("world"));
    log.debug(msg);
    return msg;
  }

}
