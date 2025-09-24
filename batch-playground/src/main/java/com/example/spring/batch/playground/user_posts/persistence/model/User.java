package com.example.spring.batch.playground.user_posts.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
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
