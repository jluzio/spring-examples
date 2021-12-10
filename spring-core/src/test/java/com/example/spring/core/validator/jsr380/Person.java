package com.example.spring.core.validator.jsr380;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {

  private String id;
  @NotEmpty
  private String name;
  @Min(1)
  @Max(110)
  private Integer age;

}
