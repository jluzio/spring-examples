package com.example.spring.tools.test

class TestSupport {
  companion object {

    const val LIVE_TEST_ENABLE_RULE: String = "#{systemEnvironment['LIVE_TEST'] == 'true'}"
  }
}