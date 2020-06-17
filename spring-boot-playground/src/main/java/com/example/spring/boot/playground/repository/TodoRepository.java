package com.example.spring.boot.playground.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.example.spring.boot.playground.model.Todo;

@RestResource(path = "todos")
public interface TodoRepository extends CrudRepository<Todo, Integer>{
}
