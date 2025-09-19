package com.example.spring.core.data_binding;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class BeanWrapperTest {

  @Test
  void test() {
    BeanWrapper bean = new BeanWrapperImpl(
        Person.builder()
            .id("id")
            .age(123)
            .name("")
            .build()
    );

    bean.setPropertyValue("name", "some-name");
    bean.setPropertyValue("hobbies", new ArrayList<>(List.of("a", "b")));
    bean.setPropertyValue("hobbies[1]", "c");

    var person = (Person) bean.getWrappedInstance();
    log.info("person: {}", person);
  }

  @Data
  @Builder
  static class Person {

    private String id;
    private String name;
    private Integer age;
    private List<String> hobbies;

  }

}
