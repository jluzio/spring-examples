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
    classes = ExcludePackageListTest.Config.class,
    properties = {"debug=true"})
class ExcludePackageListTest {

  @ComponentScan(
      basePackages = "com.example.spring.core.config.component_scan",
      excludeFilters = {
          // filter classes in root of package
          @Filter(
              type = FilterType.ASPECTJ,
              pattern = "com.example.spring.core.config.component_scan.config_b.*"
          ),
          // filter classes inside package
          @Filter(
              type = FilterType.ASPECTJ,
              pattern = "com.example.spring.core.config.component_scan.config_c..*"
          )
          // alternative using REGEX
//          @Filter(
//              type = FilterType.REGEX,
//              pattern = ".*config_b.*"
//          ),
//          @Filter(
//              type = FilterType.REGEX,
//              pattern = ".*config_c.*"
//          )
      }
  )
  static class Config {

  }

  @Autowired
  @DataBean
  List<Object> dataBeans;

  @Test
  void test() {
    assertThat(dataBeans)
        .containsExactlyInAnyOrder("configDefault-dataBean", "configA-dataBean");
  }
}
