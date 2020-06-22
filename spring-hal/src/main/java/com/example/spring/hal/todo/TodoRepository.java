package com.example.spring.hal.todo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "todos")
public interface TodoRepository extends CrudRepository<Todo, Integer>{
}
