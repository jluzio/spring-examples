package com.example.tools.flyway.persistence;

import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IdGenerators {

  public static String uuid() {
    return UUID.randomUUID().toString();
  }

}
