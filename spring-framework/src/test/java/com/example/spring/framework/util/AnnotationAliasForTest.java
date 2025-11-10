package com.example.spring.framework.util;

import com.example.spring.framework.qualifier.DebugQualifier;
import com.example.spring.framework.qualifier.GroupQualifier;
import com.example.spring.framework.qualifier.NamedGroupQualifier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
class AnnotationAliasForTest {

  @Test
  void test_annotation_attributes() {
    log.debug("debugAttributes: {}", AnnotatedElementUtils
        .getMergedAnnotationAttributes(DebugBean1.class, DebugQualifier.class));
    log.debug("namedGroupAttributes: {}", AnnotatedElementUtils
        .getMergedAnnotationAttributes(DebugBean1.class, NamedGroupQualifier.class));
    log.debug("groupAttributes: {}", AnnotatedElementUtils
        .getMergedAnnotationAttributes(DebugBean1.class, GroupQualifier.class));
    log.debug("qualifierAttributes: {}", AnnotatedElementUtils
        .getMergedAnnotationAttributes(DebugBean1.class, Qualifier.class));
  }

  @Configuration
  static class Config {

  }

  @Component
  @DebugQualifier(id = "debugBean1")
  static class DebugBean1 {

  }

}

