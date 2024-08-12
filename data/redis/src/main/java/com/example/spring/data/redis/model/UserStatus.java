package com.example.spring.data.redis.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserStatus {
  ACTIVE("active"),
  INACTIVE("inactive");

  private final String value;
}
