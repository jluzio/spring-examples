package com.example.spring.data.jpa.model.converter;


import static java.util.Optional.ofNullable;

import com.example.spring.data.jpa.model.UserStatus;
import jakarta.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Objects;

public class UserStatusConverter implements AttributeConverter<UserStatus, String> {

  @Override
  public String convertToDatabaseColumn(UserStatus attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public UserStatus convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Arrays.stream(UserStatus.values())
        .filter(userStatus -> Objects.equals(userStatus.getValue(), dbData))
        .findFirst()
        .orElseThrow();
  }
}
