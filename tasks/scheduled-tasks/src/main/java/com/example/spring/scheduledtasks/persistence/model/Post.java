package com.example.spring.scheduledtasks.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "body")
public class Post {

  @Id
  private String id;
  private String userId;
  private String title;
  private String body;

}
