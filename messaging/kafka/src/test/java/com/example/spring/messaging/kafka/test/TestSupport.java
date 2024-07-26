package com.example.spring.messaging.kafka.test;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestSupport {

  public static final String LIVE_TEST = "#{systemEnvironment['LIVE_TEST'] == 'true'}";

}
