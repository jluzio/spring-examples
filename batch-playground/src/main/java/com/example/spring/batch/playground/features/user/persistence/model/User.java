package com.example.spring.batch.playground.features.user.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity(name = "APP_USER")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

  @Id
  private String id;
  private String name;
  private String username;
  private String email;

}
