package com.example.spring.core.cache;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CacheId {

  public static final String DEFAULT = "DEFAULT";
  public static final String CALCULATIONS = "CALCULATIONS";
  public static final String MESSAGES = "MESSAGES";

  public static final String[] VALUES = {DEFAULT, CALCULATIONS, MESSAGES};

}
