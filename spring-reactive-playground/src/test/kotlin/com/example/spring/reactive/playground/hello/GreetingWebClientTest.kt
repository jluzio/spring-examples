package com.example.spring.reactive.playground.hello

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class GreetingWebClientTest {

  val log = LoggerFactory.getLogger(this.javaClass)

  @Test
  fun getResult() {
    var client = GreetingWebClient()
    val result = client.getResult()
    log.info("message: {}", result)
    assertNotNull(result)
  }
}