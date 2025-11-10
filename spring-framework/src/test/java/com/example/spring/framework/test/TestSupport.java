package com.example.spring.framework.test;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestSupport {

  public static final String LIVE_TEST_ENABLE_RULE = "#{systemEnvironment['LIVE_TEST'] == 'true'}";

}
