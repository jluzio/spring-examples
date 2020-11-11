package com.example.spring.reactive.playground

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringReactivePlaygroundApplication

fun main(args: Array<String>) {
	runApplication<SpringReactivePlaygroundApplication>(*args)
}
