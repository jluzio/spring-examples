package com.example.spring.messaging.stream.kafka.fizzbuzz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class FizzBuzzController {

  @Autowired
  private StreamBridge streamBridge;

  @RequestMapping("/fizz-buzz")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void delegateToSupplier(@RequestBody String body) {
    streamBridge.send("numbers", body);
  }

}
