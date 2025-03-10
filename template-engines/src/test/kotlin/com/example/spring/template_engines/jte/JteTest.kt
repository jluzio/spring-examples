package com.example.spring.template_engines.jte

import gg.jte.ContentType
import gg.jte.TemplateEngine
import gg.jte.output.StringOutput
import gg.jte.resolve.DirectoryCodeResolver
import java.nio.file.Path
import kotlin.test.Test

class JteTest {

  private val codeResolver = DirectoryCodeResolver(Path.of("src/main/jte")) // This is the directory where your .kte files are located.

  @Test
  fun test_example() {
    val templateEngine = TemplateEngine.create(codeResolver, ContentType.Html) // Two choices: Plain or Html

    val output = StringOutput()
    val page = Page("page_title", "page_desc")
    templateEngine.render("example.kte", page, output)
    println(output)
  }

  @Test
  fun test_example_layout() {
    val templateEngine = TemplateEngine.create(codeResolver, ContentType.Html) // Two choices: Plain or Html

    val output = StringOutput()
    val page = Page("page_title", "page_desc")
    templateEngine.render("example-layout.kte", page, output)
    println(output)
  }

}