package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
        "test-data.string=string_value",
        "test-data.long=3",
        "test-data.boolean=true",
    }
)
class ValueDataBindingTest {

  @Value("${test-data.string}")
  String stringValue;
  @Value("${test-data.boolean}")
  boolean booleanValue;
  @Value("${test-data.long}")
  long longValue;

  @Test
  void test() {
    assertThat(stringValue)
        .isEqualTo("string_value");
    assertThat(booleanValue)
        .isTrue();
    assertThat(longValue)
        .isEqualTo(3);
  }

}
