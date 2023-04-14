package com.example.spring.data.jpa.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserStatus {
  ACTIVE("active"),
  INACTIVE("inactive");

  private final String value;
}
