package com.example.spring.framework.cache;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CacheId {

  public static final String DEFAULT = "DEFAULT";
  public static final String CALCULATIONS = "CALCULATIONS";
  public static final String MESSAGES = "MESSAGES";
  public static final String MESSAGES_MULTI_KEY = "MESSAGES_MULTI_KEY";

  public static final String[] VALUES = {DEFAULT, CALCULATIONS, MESSAGES, MESSAGES_MULTI_KEY};

}
