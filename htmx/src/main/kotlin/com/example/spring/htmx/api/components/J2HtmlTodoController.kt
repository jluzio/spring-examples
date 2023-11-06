package com.example.spring.htmx.api.components

import com.example.spring.htmx.service.TodoService
import j2html.TagCreator.div
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/components/j2html/todos")
class J2HtmlTodoController {

  @Autowired
  lateinit var todoService: TodoService

  @GetMapping("", produces = [MediaType.TEXT_HTML_VALUE])
  fun todos(model: ModelMap): String {
    model["todos"] = todoService.todos
    // TODO: create the html
    return div().withId("todos-div")
      .withText("todos-div")
      .render();
  }

  @GetMapping("{id}", produces = [MediaType.TEXT_HTML_VALUE])
  fun todo(@PathVariable id: String, model: ModelMap): String {
    model["todo"] = todoService.todos.find { it.id == id }
    // TODO: create the html
    return ""
  }

}