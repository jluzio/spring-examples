package com.example.spring.webstack.web.api;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

  @ExceptionHandler
  ProblemDetail handle(IllegalStateException e) {
    // RFC 7807
    var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setDetail(e.getLocalizedMessage());
    // extension values
    pd.setProperties(Map.of(
        "some-key", "some-value"
    ));
    return pd;
  }

}
