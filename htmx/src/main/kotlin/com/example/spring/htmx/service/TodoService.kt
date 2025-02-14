package com.example.spring.htmx.service

import com.example.spring.htmx.model.Todo
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class TodoService {

  private val counter = AtomicInteger(0)

  val todos = (1..10).map {
    Todo("${counter.incrementAndGet()}", "todo-${it}", "desc-${it}")
  }.toMutableList()

  fun add(data: Todo) {
    val (_, name, description) = data
    val todo = Todo(counter.incrementAndGet().toString(), name, description)
    todos += todo
  }

}