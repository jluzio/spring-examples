package com.example.spring.framework.el;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@SpringBootTest
@Slf4j
class ExpressionLanguageTest {

  @Configuration
  static class Config {

  }

  @Value
  static class RootBean {

    String foo;
  }

  @Test
  void test() {
    ExpressionParser exprParser = getExpressionParser();

    Expression expr1 = exprParser.parseExpression("'Hello World'");
    assertThat(expr1.getValue(String.class))
        .isInstanceOf(String.class)
        .isEqualTo("Hello World");

    StandardEvaluationContext evaluationContext = new StandardEvaluationContext(
        new RootBean("bar")
    );

    Expression expr2 = exprParser.parseExpression("foo");
    assertThat(expr2.getValue(evaluationContext, String.class))
        .isInstanceOf(String.class)
        .isEqualTo("bar");

    var parserContext = new TemplateParserContext();
    Expression expr3 = exprParser.parseExpression("#{foo}", parserContext);
    assertThat(expr3.getValue(evaluationContext, String.class))
        .isInstanceOf(String.class)
        .isEqualTo("bar");

    Expression expr4 = exprParser.parseExpression("#{T(com.google.common.base.Strings).padStart(foo, 5, '.')}",
        parserContext);
    assertThat(expr4.getValue(evaluationContext, String.class))
        .isInstanceOf(String.class)
        .isEqualTo("..bar");
  }

  private ExpressionParser getExpressionParser() {
    SpelParserConfiguration config = new SpelParserConfiguration(
        // enable configuration of expressions
        SpelCompilerMode.IMMEDIATE,
        this.getClass().getClassLoader()
    );
    return new SpelExpressionParser(config);
  }

}
