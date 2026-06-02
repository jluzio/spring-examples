package com.example.spring.framework.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
        "test-data.string: string_value",
        "test-data.long: 3",
        "test-data.boolean: true",
        "test-data.c_csv: 1,2,3,4",
        "test-data.s_csv: 1;2;3;4",
        "test-data.mixed_csv: 1,2,3;4",
    }
)
class ValueDataBindingTest {

  @Value("${test-data.string}")
  String stringValue;
  @Value("${test-data.boolean}")
  boolean booleanValue;
  @Value("${test-data.long}")
  long longValue;
  @Value("${test-data.c_csv}")
  List<String> cCsvValue;
  @Value("${test-data.s_csv}")
  List<String> sCsvValue;
  @Value("${test-data.mixed_csv}")
  List<String> mixedCsvValue;

  @Test
  void test() {
    assertThat(stringValue)
        .isEqualTo("string_value");
    assertThat(booleanValue)
        .isTrue();
    assertThat(longValue)
        .isEqualTo(3);
    assertThat(cCsvValue)
        .containsExactly("1", "2", "3", "4");
    assertThat(sCsvValue)
        .containsExactly("1;2;3;4");
    assertThat(mixedCsvValue)
        .containsExactly("1", "2", "3;4");
  }

}
