package com.example.spring.boot.playground.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"key", "message"})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ApiErrorException extends RuntimeException {

  @JsonProperty("key")
  private String key;

  protected ApiErrorException() {
    super();
  }

  protected ApiErrorException(String message) {
    super(message);
  }

  protected ApiErrorException(String message, Throwable cause) {
    super(message, cause);
  }

  protected ApiErrorException(String key, String message) {
    super(message);
    this.key = key;
  }

  protected ApiErrorException(String key, String message, Throwable cause) {
    super(message, cause);
    this.key = key;
  }

  @JsonProperty("message")
  public String getMessage() {
    return super.getMessage();
  }
}
