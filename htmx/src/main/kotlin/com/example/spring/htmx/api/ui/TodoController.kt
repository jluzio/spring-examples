package com.example.spring.htmx.api.ui

import com.example.spring.htmx.service.TodoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class TodoController {

  @Autowired
  lateinit var todoService: TodoService

  @GetMapping("/ui/todos")
  fun todos(model: ModelMap): String {
    model["todos"] = todoService.todos
    return "ui/todos"
  }

  @GetMapping("/ui/todos/{id}")
  fun todo(@PathVariable id: String, model: ModelMap): String {
    model["todo"] = todoService.todos.find { it.id == id }
    return "ui/todo"
  }

}