package com.example.spring.boot.playground.user;

import com.example.spring.boot.playground.query.PageRequestData;

import lombok.Data;

@Data
public class UserFilter {
  private User user;
  private PageRequestData page;
}
