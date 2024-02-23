package com.example.spring.core.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

  @ExceptionHandler
  ProblemDetail handle(IllegalStateException e) {
    var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setDetail(e.getLocalizedMessage());
    return pd;
  }

}
