package com.example.spring.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalRestControllerExceptionHandler {

  @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
  @ExceptionHandler({UnsupportedOperationException.class})
  public void handleUnsupportedOperation() {
    // nothing to add
  }

}
