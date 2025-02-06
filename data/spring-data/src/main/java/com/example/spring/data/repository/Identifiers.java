package com.example.spring.data.repository;

import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Identifiers {

  public static String generateUuid() {
    return UUID.randomUUID().toString();
  }

}
