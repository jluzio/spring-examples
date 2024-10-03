package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.geom.Rectangle2D;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;

@SpringBootTest(
    properties = {
        "configuration-properties-binding-test.rectangle2DVal1=1,2,3,4",
        "configuration-properties-binding-test.rectangle2DVal2=",
    }
)
class ConfigurationPropertiesBindingTest {

  @Configuration
  @Import({Rectangle2DConverter.class})
  @EnableConfigurationProperties(ConfigProperties.class)
  static class Config {

  }

  @ConfigurationProperties("configuration-properties-binding-test")
  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ConfigProperties {

    private Rectangle2D rectangle2DVal1;
    private Rectangle2D rectangle2DVal2;
    private Rectangle2D rectangle2DVal3;

  }

  @ConfigurationPropertiesBinding
  public static class Rectangle2DConverter implements Converter<String, Rectangle2D> {

    @Override
    public Rectangle2D convert(String source) {
      if (source.isBlank()) {
        return null;
      }
      // Parse the string and create a Rectangle2D object
      String[] parts = source.split(",");
      if (parts.length == 4) {
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double width = Double.parseDouble(parts[2]);
        double height = Double.parseDouble(parts[3]);
        return new Rectangle2D.Double(x, y, width, height);
      } else {
        throw new IllegalArgumentException("Invalid Rectangle2D format: " + source);
      }
    }
  }

  @Autowired
  ConfigProperties properties;

  @Test
  void test() {
    assertThat(properties)
        .isEqualTo(ConfigProperties.builder()
            .rectangle2DVal1(new Rectangle2D.Double(1, 2, 3, 4))
            .build());
  }

}
