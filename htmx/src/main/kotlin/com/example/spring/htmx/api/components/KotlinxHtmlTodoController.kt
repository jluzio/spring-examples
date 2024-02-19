package com.example.spring.htmx.api.components

import com.example.spring.htmx.service.TodoService
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/components/kotlinx-html/todos")
class KotlinxHtmlTodoController {

  @Autowired
  lateinit var todoService: TodoService

  @GetMapping("", produces = [MediaType.TEXT_HTML_VALUE])
  fun todos(model: ModelMap): String {
    val todos = todoService.todos
    return buildString {
      appendHTML().div {
        table {
          classes = setOf("table", "table-striped", "table-sm")
          thead {
            tr {
              listOf("id", "name", "description", "actions").map {
                th {
                  classes = setOf("col")
                  +it
                }
              }
            }
          }
          tbody {
            todos.map {
              tr {
                attributes["hx-get"] = "/components/kotlinx-html/todos/${it.id}"
                attributes["hx-trigger"] = "click"
                attributes["hx-target"] = "#todos-item"
                attributes["hx-swap"] = "innerHTML"
                th {
                  scope = ThScope.row
                  +it.id
                }
                td {
                  +it.name
                }
                td {
                  +it.description
                }
                td {

                }
              }
            }
          }
        }
        div {
          id = "todo-div"
        }
      }
    }
  }

  @GetMapping("{idParam}", produces = [MediaType.TEXT_HTML_VALUE])
  fun todo(@PathVariable idParam: String, model: ModelMap): String {
    val todo = todoService.todos.find { it.id == idParam }!!
    return buildString {
      appendHTML().div {
        id = "todos-item-inner"
        classes = setOf("card", "row", "offset-md-3", "col-md-6")
        div {
          classes = setOf("card-body")
          h5 {
            classes = setOf("card-title")
            +todo.name
          }
          p {
            classes = setOf("card-text")
            +todo.description
          }
          button {
            type = ButtonType.button
            classes = setOf("btn", "btn-primary")
            attributes["_"] = "on click remove #todos-item-inner"
            +"Close"
          }
        }
      }
    }
  }

}