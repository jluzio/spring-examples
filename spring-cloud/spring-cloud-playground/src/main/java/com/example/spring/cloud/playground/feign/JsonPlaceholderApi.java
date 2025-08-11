package com.example.spring.cloud.playground.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("json-placeholder")
public interface JsonPlaceholderApi {

  @GetMapping("/users")
  List<User> users();

  @GetMapping("/users/{id}")
  User user(@PathVariable String id);

}
