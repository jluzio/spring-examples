package com.example.spring.framework.validator.jsr380;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {

  private String id;
  @NotEmpty
  private String name;
  @Min(value = 18, message = "{validation.constraints.MinCustom.message}")
  @Max(110)
  private Integer age;
  @NotNull(message = "hobbies must not be null")
  @Builder.Default
  private List<@NotEmpty(message = "hobby must not be empty") String> hobbies = new ArrayList<>();

}
