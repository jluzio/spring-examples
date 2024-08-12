package com.example.spring.data.mongodb.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  private String id;
  private String name;
  private String username;
  private UserStatus status;
  private Instant createdAt;

}
