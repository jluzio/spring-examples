package com.example.spring.htmx.api.components

import com.example.spring.htmx.service.TodoService
import j2html.TagCreator.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
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
    val todos = todoService.todos
    // @formatter:off
    return div(
      table(
        attrs(".table.table-striped.table-sm"),
        thead(
          tr(
            *arrayOf("id", "name", "description", "actions").map {
              th(it).withClass("col")
            }.toTypedArray()
          )
        ),
        tbody(
          *todos.map {
            tr(
              *listOf(
                th(it.id).attr("scope", "row"),
                td(it.name),
                td(it.description),
                td()
              ).toTypedArray()
            )
              .attr("hx-get", "/components/j2html/todos/${it.id}")
              .attr("hx-trigger", "click")
              .attr("hx-target", "#todos-item")
              .attr("hx-swap", "innerHTML")
          }.toTypedArray()
        )
      ),
      div(attrs("#todo-div"))
    )
      .render();
    // @formatter:on
  }

  @GetMapping("{id}", produces = [MediaType.TEXT_HTML_VALUE])
  fun todo(@PathVariable id: String, model: ModelMap): String {
    val todo = todoService.todos.find { it.id == id }!!
    // @formatter:off
    return div(
      attrs("#todos-item-inner.card.row.offset-md-3.col-md-6"),
      div(
        attrs(".card-body"),
        h5(todo.name).withClass("card-title"),
        p(todo.description).withClass("card-text"),
        button("Close")
          .withType("button")
          .withClass("btn btn-primary")
          .attr("_", "on click remove #todos-item-inner")
      )
    )
      .render()
    // @formatter:on
  }

}