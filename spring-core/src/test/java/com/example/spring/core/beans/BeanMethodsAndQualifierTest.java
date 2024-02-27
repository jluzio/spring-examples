package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Data;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(
    properties = {
        "app.cfg-props.main.id=idMain",
        "app.cfg-props.1.id=id1",
        "app.cfg-props.2.id=id2",
        "app.cfg-props.3.id=id3",
    }
)
@EnableConfigurationProperties
class BeanMethodsAndQualifierTest {

  @Data
  static class CfgProps {

    private String id;
  }

  @Value
  static class HolderCfgProps {

    CfgProps cfgProps;
  }

  @Configuration
  static class Config {

    @Bean
    @ConfigurationProperties("app.cfg-props.main")
    CfgProps cfgPropsMain() {
      return new CfgProps();
    }

    @Bean
    @ConfigurationProperties("app.cfg-props.1")
    CfgProps cfgProps1() {
      return new CfgProps();
    }

    @Bean
    @ConfigurationProperties("app.cfg-props.2")
    CfgProps cfgProps2() {
      return new CfgProps();
    }

    @Bean
    @ConfigurationProperties("app.cfg-props.3")
    CfgProps cfgProps3() {
      return new CfgProps();
    }

    @Bean
    HolderCfgProps holderCfgProps(CfgProps cfgProps2) {
      return new HolderCfgProps(cfgProps2);
    }
  }

  @Autowired
  CfgProps cfgProps1;
  @Autowired
  CfgProps cfgProps2;
  @Autowired
  CfgProps cfgProps3;
  @Autowired
  HolderCfgProps holderCfgProps;

  @Test
  void verify_props() {
    assertThat(cfgProps1)
        .isNotEqualTo(cfgProps2)
        .isNotEqualTo(cfgProps3)
        .extracting(CfgProps::getId)
        .isEqualTo("id1");
    assertThat(cfgProps2)
        .isNotEqualTo(cfgProps3)
        .extracting(CfgProps::getId)
        .isEqualTo("id2");
    assertThat(cfgProps3)
        .extracting(CfgProps::getId)
        .isEqualTo("id3");

    assertThat(holderCfgProps)
        .extracting(HolderCfgProps::getCfgProps)
        .extracting(CfgProps::getId)
        .isEqualTo("id2");
  }

}
