package com.example.spring.core.config.component_scan;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootTest(
    classes = ExcludeAllPackagesTest.Config.class,
    properties = {"debug=true"})
class ExcludeAllPackagesTest {

  @ComponentScan(
      basePackages = "com.example.spring.core.config.component_scan",
      excludeFilters = @Filter(
          type = FilterType.ASPECTJ,
          pattern = "com.example.spring.core.config.component_scan..*"
      )
  )
  static class Config {

  }

  @Autowired(required = false)
  @DataBean
  List<Object> dataBeans;

  @Test
  void test() {
    assertThat(dataBeans)
        .isNull();
  }
}
