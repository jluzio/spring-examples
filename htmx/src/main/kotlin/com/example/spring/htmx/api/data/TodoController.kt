package com.example.spring.htmx.api.data

import com.example.spring.htmx.service.TodoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/todos")
class TodoController {

  @Autowired
  lateinit var todoService: TodoService

  @GetMapping("")
  fun todos(model: ModelMap) = todoService.todos

  @GetMapping("{id}")
  fun todo(@PathVariable id: String, model: ModelMap) = todoService.todos.find { it.id == id }

}