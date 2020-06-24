package com.example.spring.cdi;

import com.example.spring.cdi.MetaAnnotationsTest.Config.CustomScopedDefaultBean;
import com.example.spring.cdi.MetaAnnotationsTest.Config.CustomScopedSingletonBean;
import com.example.spring.cdi.stereotype.CustomScope;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
public class MetaAnnotationsTest {

  @Autowired
  CustomScopedDefaultBean customScopedDefaultBean1;
  @Autowired
  CustomScopedDefaultBean customScopedDefaultBean2;
  @Autowired
  CustomScopedDefaultBean customScopedDefaultBean3;
  @Autowired
  CustomScopedSingletonBean customScopedSingletonBean1;
  @Autowired
  CustomScopedSingletonBean customScopedSingletonBean2;
  @Autowired
  CustomScopedSingletonBean customScopedSingletonBean3;

  @Test
  void test() {
    log.debug("customScopedDefaultBeans: {}", Lists
        .newArrayList(customScopedDefaultBean1, customScopedDefaultBean2,
            customScopedDefaultBean3));
    log.debug("customScopedSingletonBeans: {}", Lists
        .newArrayList(customScopedSingletonBean1, customScopedSingletonBean2,
            customScopedSingletonBean3));
  }

  @Configuration
  static class Config {

    interface SampleBean {

      String sayHello();
    }

    @Component
    @CustomScope
    static class CustomScopedDefaultBean {

      public String sayHello() {
        return "hello";
      }
    }

    @Component
    @CustomScope("singleton")
    static class CustomScopedSingletonBean {

      public String sayHello() {
        return "hello";
      }
    }
  }

}
