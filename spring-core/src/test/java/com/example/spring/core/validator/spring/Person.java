package com.example.spring.core.validator.spring;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {

  private String id;
  private String name;
  private Integer age;

}
