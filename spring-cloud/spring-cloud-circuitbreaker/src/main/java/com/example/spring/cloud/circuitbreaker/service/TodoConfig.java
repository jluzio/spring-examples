package com.example.spring.cloud.circuitbreaker.service;

import com.example.spring.cloud.circuitbreaker.model.Todo;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("todo-service")
@Data
public class TodoConfig {

  private List<Todo> todos = new ArrayList<>();

}
