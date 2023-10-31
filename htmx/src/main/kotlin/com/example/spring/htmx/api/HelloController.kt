package com.example.spring.htmx.api

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@Controller
class HelloController {

  @GetMapping("/hello/{name}")
  fun hello(@PathVariable name: String, model: ModelMap): String {
    model["name"] = name
    return "hello"
  }

}