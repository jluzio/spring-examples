package com.example.spring.cloud.playground.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public final class User {

  private int id;
  private String name;
  private String username;
  private String email;

}
