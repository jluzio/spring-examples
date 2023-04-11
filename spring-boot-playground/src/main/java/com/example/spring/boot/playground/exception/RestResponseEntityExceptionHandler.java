package com.example.spring.boot.playground.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @Value
  @Builder
  @JsonInclude(Include.NON_NULL)
  public static class ErrorInfo {

    String key;
    String message;
  }

  @ExceptionHandler({ApiErrorException.class})
  protected ResponseEntity<Object> handleApiErrorException(
      ApiErrorException ex,
      WebRequest request
  ) {
    ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
    var errorInfo = ErrorInfo.builder()
        .key(ex.getKey())
        .message(ex.getMessage())
        .build();
    return handleExceptionInternal(
        ex, errorInfo, new HttpHeaders(), responseStatus.value(), request);
  }
}