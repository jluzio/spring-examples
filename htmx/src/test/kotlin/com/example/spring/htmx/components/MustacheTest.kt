package com.example.spring.htmx.components

import com.example.spring.htmx.model.Todo
import com.samskivert.mustache.Mustache
import groovy.util.logging.Slf4j
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory


@Slf4j
class MustacheTest {

  var log = LoggerFactory.getLogger(javaClass)

  data class Item(val id: String, val name: String)

  @Test
  fun test_helloWorld() {
    val source = "Hello {{arg}}!"
    val template = Mustache.compiler().compile(source)
    val context = mapOf(
      "arg" to "world"
    )
    assertThat(template.execute(context))
      .isEqualTo("Hello world!")
  }

  @Test
  fun test_collection() {
    val source = """
      <div>
        <table class="table table-striped table-sm">
          <thead>
          <tr>
            <th scope="col">id</th>
            <th scope="col">name</th>
            <th scope="col">description</th>
            <th scope="col">actions</th>
          </tr>
          </thead>
          <tbody>
          {{#.}}
          <tr hx-get="/todos/{{id}}"
              hx-trigger="click"
              hx-target="#todo-cs-div"
              hx-swap="outerHTML"
          >
            <th scope="row">
              {{id}}
            </th>
            <td>
              {{name}}
            </td>
            <td>
              {{description}}
            </td>
            <td>
            </td>
          </tr>
          {{/.}}
          </tbody>
        </table>
        <div id="todo-cs-div"></div>
      </div>
    """
    val template = Mustache.compiler().compile(source)
    val context = (1..10)
      .map { Todo(it.toString(), "Item-$it", "Desc-$it") }
      .toList()
    assertThat(template.execute(context))
      .satisfies(log::info)
      .isNotEmpty()
  }

}