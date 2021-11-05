package com.example.spring.stream.playground.ping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class PingController {

  @Autowired
  private StreamBridge streamBridge;

  @RequestMapping("/ping")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void delegateToSupplier(@RequestBody String body) {
    streamBridge.send("ping", body);
  }

}
