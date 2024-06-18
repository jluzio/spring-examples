package com.example.spring.data.redis.model;

import java.time.OffsetDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("user")
public class User {

  @Id
  private String id;
  private String name;
  private String email;
  private UserStatus status;
  private OffsetDateTime createdAt;

}