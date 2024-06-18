package com.example.spring.data.mongodb.model;

import com.example.spring.data.mongodb.converter.OffsetDateTimeValueConverter;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.convert.ValueConverter;

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
  @ValueConverter(OffsetDateTimeValueConverter.class)
  private OffsetDateTime createdAt;

}
