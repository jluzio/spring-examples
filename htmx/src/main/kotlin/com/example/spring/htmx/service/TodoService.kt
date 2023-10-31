package com.example.spring.htmx.service

import com.example.spring.htmx.model.Todo
import org.springframework.stereotype.Service

@Service
class TodoService {

  val todos = (1..10).map {
    Todo("$it", "todo-${it}", "desc-${it}")
  }.toMutableList()

}