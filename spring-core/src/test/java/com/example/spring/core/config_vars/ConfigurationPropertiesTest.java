package com.example.spring.core.config_vars;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.geom.Rectangle2D;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;

@SpringBootTest(
    properties = {
        "test-props.bean.value1=value1",
        "test-props.bean.value2=value2",
        "test-props.bean2.value1=value1_2",
        "test-props.bean2.value2=value2_2",
        "test-props.constructor.value1=value1",
        "test-props.constructor.value2=value2",
        "test-props.constructor-customize.value1And2=value1,value2",
        "test-props.converter.rectangle2DVal1=1,2,3,4",
        "test-props.converter.rectangle2DVal2=",
    }
)
class ConfigurationPropertiesTest {

  @Configuration
  @Import({Rectangle2DConverter.class})
  @EnableConfigurationProperties({BeanProps.class, ValueConstructorBindingProps.class,
      RecordConstructorBindingProps.class, CustomizeConstructorBindingProps.class, ConverterProps.class})
  static class Config {

    @Bean
    @ConfigurationProperties("test-props.bean2")
    AnotherBeanProps beanProps2() {
      return new AnotherBeanProps();
    }
  }

  @ConfigurationProperties("test-props.bean")
  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class BeanProps {

    private String value1;
    private String value2;
  }

  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AnotherBeanProps {

    private String value1;
    private String value2;
  }

  @ConfigurationProperties("test-props.constructor")
  @Value
  @Builder
  public static class ValueConstructorBindingProps {

    String value1;
    String value2;
  }

  @ConfigurationProperties("test-props.constructor")
  @Builder
  public record RecordConstructorBindingProps(String value1, String value2) {

  }

  @ConfigurationProperties("test-props.constructor-customize")
  @Value
  @Builder
  @AllArgsConstructor
  public static class CustomizeConstructorBindingProps {

    String value1;
    String value2;
    String value3;

    @ConstructorBinding
    public CustomizeConstructorBindingProps(String value1And2, @DefaultValue("value3-default") String value3) {
      this(substringBefore(value1And2, ","), substringAfter(value1And2, ","), value3);
    }

  }

  @ConfigurationProperties("test-props.converter")
  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ConverterProps {

    private Rectangle2D rectangle2DVal1;
    private Rectangle2D rectangle2DVal2;
    private Rectangle2D rectangle2DVal3;
  }

  @Autowired
  BeanProps beanProps;
  @Autowired
  AnotherBeanProps beanProps2;
  @Autowired
  ValueConstructorBindingProps valueConstructorBindingProps;
  @Autowired
  RecordConstructorBindingProps recordConstructorBindingProps;
  @Autowired
  CustomizeConstructorBindingProps customizeConstructorBindingProps;
  @Autowired
  ConverterProps converterProps;

  @Test
  void test_beanProps() {
    assertThat(beanProps)
        .isEqualTo(BeanProps.builder()
            .value1("value1")
            .value2("value2")
            .build());
    assertThat(beanProps2)
        .isEqualTo(AnotherBeanProps.builder()
            .value1("value1_2")
            .value2("value2_2")
            .build());
  }

  @Test
  void test_beanProps_appCtxRunner() {
    new ApplicationContextRunner()
        .withUserConfiguration(Config.class)
        // ConfigDataApplicationContextInitializer is required on certain scenarios
        // but is not required in this one due to @EnableConfigurationProperties
        .withInitializer(new ConfigDataApplicationContextInitializer())
        .withPropertyValues(
            "test-props.bean.value1=value1_2",
            "test-props.bean.value2=value2_2"
        ).run(ctx -> {
          var appCtxRunnerBeanProps = ctx.getBean(BeanProps.class);
          assertThat(appCtxRunnerBeanProps)
              .isEqualTo(BeanProps.builder()
                  .value1("value1_2")
                  .value2("value2_2")
                  .build());
        });
  }

  @Test
  void test_constructorBindingProps() {
    assertThat(valueConstructorBindingProps)
        .isEqualTo(ValueConstructorBindingProps.builder()
            .value1("value1")
            .value2("value2")
            .build());
    assertThat(recordConstructorBindingProps)
        .isEqualTo(RecordConstructorBindingProps.builder()
            .value1("value1")
            .value2("value2")
            .build());
  }

  @Test
  void test_customizeConstructorBindingProps() {
    assertThat(customizeConstructorBindingProps)
        .isEqualTo(CustomizeConstructorBindingProps.builder()
            .value1("value1")
            .value2("value2")
            .value3("value3-default")
            .build());
  }

  @Test
  void test_converterProps() {
    assertThat(converterProps)
        .isEqualTo(ConverterProps.builder()
            .rectangle2DVal1(new Rectangle2D.Double(1, 2, 3, 4))
            .build());
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
}
