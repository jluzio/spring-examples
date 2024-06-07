package com.example.spring.data.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  private String id;
  private String firstName;
  private String lastName;
  private String username;

}
