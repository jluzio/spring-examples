package com.example.spring.htmx.api.components

import com.example.spring.htmx.service.TodoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

private const val COMPONENTS = "components/mustache"

@Controller
@RequestMapping("/components/mustache/todos")
class MustacheTodoController {

  @Autowired
  lateinit var todoService: TodoService

  @GetMapping("")
  fun todos(model: ModelMap): String {
    model["todos"] = todoService.todos
    return "$COMPONENTS/todos"
  }

  @GetMapping("{id}")
  fun todo(@PathVariable id: String, model: ModelMap): String {
    model["todo"] = todoService.todos.find { it.id == id }
    return "$COMPONENTS/todo"
  }

}