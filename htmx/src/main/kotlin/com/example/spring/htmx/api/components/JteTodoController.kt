package com.example.spring.htmx.api.components

import com.example.spring.htmx.service.TodoService
import gg.jte.TemplateEngine
import gg.jte.output.StringOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/components/jte/todos")
class JteTodoController {

  @Autowired
  lateinit var todoService: TodoService
  @Autowired
  lateinit var templateEngine: TemplateEngine

  @GetMapping("", produces = [MediaType.TEXT_HTML_VALUE])
  fun todos(model: ModelMap): String {
    val todos = todoService.todos
    model["todos"] = todos
    return render("todos.kte", todos)
  }

  @GetMapping("{id}", produces = [MediaType.TEXT_HTML_VALUE])
  fun todo(@PathVariable id: String, model: ModelMap): String {
    val todo = todoService.todos.find { it.id == id }
    model["todo"] = todo
    return render("todo.kte", todo)
  }

  fun render(name: String, model: Any?): String {
    val output = StringOutput()
    templateEngine.render(name, model, output)
    val html = output.toString()
    return html
  }

}