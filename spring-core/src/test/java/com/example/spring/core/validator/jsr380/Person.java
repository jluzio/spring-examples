package com.example.spring.core.validator.jsr380;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
