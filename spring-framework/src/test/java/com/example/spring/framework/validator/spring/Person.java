package com.example.spring.framework.validator.spring;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {

  private String id;
  private String name;
  private Integer age;

}
