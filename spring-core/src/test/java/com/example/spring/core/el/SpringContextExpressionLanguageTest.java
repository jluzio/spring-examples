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
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
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

  enum UserRole {
    USER,
    ADMIN;
  }

  record UserData(String username, Object role) {

  }


  @Autowired
  ConfigurableApplicationContext applicationContext;
  @Autowired
  ConfigurableBeanFactory beanFactory;

  @Test
  void test_simple_getValue_string() {
    ExpressionParser exprParser = getExpressionParser();
    Expression expression = exprParser.parseExpression("role == 'ADMIN'");

    var context = new StandardEvaluationContext(new UserData("u1", "ADMIN"));
    assertThat(expression.getValue(context))
        .isInstanceOf(Boolean.class)
        .isEqualTo(true);
  }

  @Test
  void test_simple_getValue_enum() {
    ExpressionParser exprParser = getExpressionParser();

    var userData = new UserData("u1", UserRole.ADMIN);
    var context = new StandardEvaluationContext(userData);

    Expression expression1 = exprParser.parseExpression("role == 'ADMIN'");
    assertThat(expression1.getValue(context))
        .isEqualTo(false);

    Expression expression2 = exprParser.parseExpression("role.name == 'ADMIN'");
    assertThat(expression2.getValue(context))
        .isEqualTo(true);
  }

  @Test
  void test_bean_reference() {
    ExpressionParser exprParser = getExpressionParser();

    StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));

    Expression expr1 = exprParser.parseExpression("@foo");
    assertThat(expr1.getValue(evaluationContext, String.class))
        .isInstanceOf(String.class)
        .isEqualTo("bar");
  }

  @Test
  void test_bean_reference_bean_expression() {
    ExpressionParser exprParser = getExpressionParser();

    StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
    evaluationContext.addPropertyAccessor(new BeanExpressionContextAccessor());

    BeanExpressionContext expressionContext = new BeanExpressionContext(beanFactory, null);

    Expression expr1 = exprParser.parseExpression("foo");
    assertThat(expr1.getValue(evaluationContext, expressionContext))
        .isInstanceOf(String.class)
        .isEqualTo("bar");

    Expression expr2 = exprParser.parseExpression("#{foo.length}", new TemplateParserContext());
    assertThat(expr2.getValue(evaluationContext, expressionContext))
        .isInstanceOf(Integer.class)
        .isEqualTo(3);
  }

  @Test
  void test_bean_expression_resolver() {
    BeanExpressionResolver expressionResolver = beanFactory.getBeanExpressionResolver();
    assertThat(expressionResolver).isNotNull();

    BeanExpressionContext expressionContext = new BeanExpressionContext(beanFactory, null);
    Object evaluate = expressionResolver.evaluate("#{foo}", expressionContext);
    assertThat(evaluate)
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

  private ExpressionParser getExpressionParser() {
    SpelParserConfiguration config = new SpelParserConfiguration(
        // enable configuration of expressions
        SpelCompilerMode.IMMEDIATE,
        this.getClass().getClassLoader()
    );
    return new SpelExpressionParser(config);
  }

}
