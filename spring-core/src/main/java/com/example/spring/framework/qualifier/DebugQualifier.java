package com.example.spring.framework.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE,
    ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@NamedGroupQualifier(group = "debug")
public @interface DebugQualifier {

  @AliasFor(annotation = NamedGroupQualifier.class)
  String value() default "";

  @AliasFor(annotation = NamedGroupQualifier.class, attribute = "value")
  String id() default "";
}
