package com.example.spring.boot.playground.model;

import lombok.Data;

@Data
public class UserFilter {

  private User user;
  private PageRequestData page;
}
