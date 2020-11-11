package com.example.spring.reactive.playground.hello

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient


class GreetingWebClient {

  private val client = WebClient.create("http://localhost:8080")
  private val result = client.get()
      .uri("/hello")
      .accept(MediaType.TEXT_PLAIN)
      .exchange();

  fun getResult() = ">> result = ${
    result.flatMap { res -> res.bodyToMono(String::class.java) }.block()
  }"

}