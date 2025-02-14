package com.example.spring.htmx.api.components

import com.example.spring.htmx.service.TodoService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.view.FragmentsRendering

private const val COMPONENTS = "components/thymeleaf"

@Controller
@RequestMapping("/components/thymeleaf/todos")
class ThymeleafTodoController {

  @Autowired
  lateinit var todoService: TodoService

  @GetMapping("")
  fun todos(model: ModelMap): String {
    model["todos"] = todoService.todos
    return "$COMPONENTS/todos"
  }

  @GetMapping("{id}")
  fun todo(@PathVariable id: String, model: ModelMap) = todoFragments(id, model)

  @GetMapping("{id}/default")
  fun todoDefault(@PathVariable id: String, model: ModelMap): String {
    model["todo"] = todoService.todos.find { it.id == id }
    return "$COMPONENTS/todo"
  }

  @GetMapping("{id}/fragments")
  @HxRequest
  fun todoFragments(@PathVariable id: String, model: ModelMap): FragmentsRendering {
    model["todo"] = todoService.todos.find { it.id == id }
    return FragmentsRendering
      .with("$COMPONENTS/todo")
      .fragment("$COMPONENTS/message")
      .build()
  }
}