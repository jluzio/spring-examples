package com.example.spring.framework.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    classes = JacksonAutoConfiguration.class,
    properties = {
        "spring.jackson.deserialization.FAIL_ON_NULL_FOR_PRIMITIVES: false"
    })
class JacksonConfigTest {

  @Builder
  record SomeBean(int id, String value) {

  }

  @Autowired
  ObjectMapper objectMapper;

  @Test
  void test() {
    var json = """
        { "value": "test" }
        """;

    var output = objectMapper.readValue(json, SomeBean.class);
    assertThat(output)
        .isEqualTo(SomeBean.builder()
            .id(0)
            .value("test")
            .build());

    var objectMapperDefault = new JsonMapper();
    assertThatThrownBy(() -> objectMapperDefault.readValue(json, SomeBean.class))
        .isInstanceOf(DatabindException.class);
  }

}
