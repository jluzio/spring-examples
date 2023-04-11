/**
 * Copyright 2016 SmartBear Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.spring.boot.playground.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends ApiErrorException {

  public BadRequestException() {
  }

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public BadRequestException(String key, String message) {
    super(key, message);
  }

  public BadRequestException(String key, String message, Throwable cause) {
    super(key, message, cause);
  }

  @Override
  @JsonProperty("key")
  public String getKey() {
    return super.getKey();
  }

  @Override
  @JsonProperty("message")
  public String getMessage() {
    return super.getMessage();
  }
}
