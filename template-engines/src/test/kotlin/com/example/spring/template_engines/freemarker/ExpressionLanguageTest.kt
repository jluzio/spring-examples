package com.example.spring.template_engines.freemarker

import freemarker.ext.beans.BeansWrapperBuilder
import freemarker.ext.beans.MapModel
import freemarker.template.Configuration
import org.assertj.core.api.Assertions.assertThat
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.StringWriter
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.test.Test


@SpringBootTest
class ExpressionLanguageTest {

  @Autowired
  lateinit var freemarkerConfig: Configuration
  val log = LoggerFactory.getLogger(ExpressionLanguageTest::class.java)
  val DEFAULT_USER: User = User("default", "default@server.org")
  val ALL_USERS: List<User> = listOf(DEFAULT_USER)

  @Test
  fun test() {
    assertThat(freemarkerConfig).isNotNull()

    val template = freemarkerConfig.getTemplate("el.ftl")
    assertThat(template).isNotNull()

    val users = IntStream.rangeClosed(1, 5)
      .mapToObj { i: Int ->
        User(
          String.format("username%s", i),
          String.format("email%s@server.org", i)
        )
      }
      .collect(Collectors.toList())

    val outputWriter = StringWriter()

    val data = mapOf("users" to users)
    val model = MapModel(
      data,
      BeansWrapperBuilder(Configuration.getVersion())
        .build()
    )

    template.process(model, outputWriter)
    log.info("{}", outputWriter)
  }

}