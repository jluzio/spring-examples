package com.example.spring.batch.playground.features.resilience.batch;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JobParameterNames {

  public static final String RANDOM = "random";
  public static final String RUN_ID = "runId";
  public static final String RE_RUN_ID = "reRunId";
  public static final String DATA = "data";
  public static final String FAILURES_MAP = "failuresMap";
  public static final String BACK_OFF_PERIOD = "backOffPeriod";

}
