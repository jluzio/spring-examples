package com.example.spring.boot.playground.api;

import com.example.spring.boot.playground.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("exceptions")
@Slf4j
public class ExceptionHandlingResource {

  @GetMapping("api-error")
  public ResponseEntity<Void> handleApiErrorException() {
    log.debug("handleApiErrorException");
    throw new BadRequestException("handleApiErrorException :: BadRequest");
  }

  @GetMapping("response-status")
  public ResponseEntity<Void> handleResponseStatusException() {
    log.debug("handleResponseStatusException");
    throw new ResponseStatusException(
        HttpStatusCode.valueOf(400), "handleResponseStatusException :: BadRequest");
  }

}
