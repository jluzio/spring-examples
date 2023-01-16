package com.example.spring.core.el;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@SpringBootTest
@Slf4j
class SpringContextExpressionLanguageTest {

  @Configuration
  static class Config {

    @Bean("foo")
    String foo() {
      return "bar";
    }
  }

  @Autowired
  ConfigurableApplicationContext applicationContext;
  @Autowired
  ConfigurableBeanFactory beanFactory;

  @Test
  void test_default() {
    ExpressionParser exprParser = getExpressionParser();

    StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));

    Expression expr = exprParser.parseExpression("@foo");
    assertThat(expr.getValue(evaluationContext, String.class))
        .isInstanceOf(String.class)
        .isEqualTo("bar");
  }

  @Test
  void test_embedded_value_resolver() {
    EmbeddedValueResolver valueResolver = new EmbeddedValueResolver(beanFactory);

    assertThat(valueResolver.resolveStringValue("#{foo}"))
        .isInstanceOf(String.class)
        .isEqualTo("bar");
  }

  @Test
  void test_bean_expression_resolver() {
    BeanExpressionResolver expressionResolver = applicationContext.getBeanFactory()
        .getBeanExpressionResolver();
    assertThat(expressionResolver).isNotNull();

    BeanExpressionContext expressionContext = new BeanExpressionContext(beanFactory, null);
    Object evaluate = expressionResolver.evaluate("#{foo}", expressionContext);
    assertThat(evaluate)
        .isInstanceOf(String.class)
        .isEqualTo("bar");
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
