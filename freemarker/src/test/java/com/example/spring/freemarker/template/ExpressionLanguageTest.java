package com.example.spring.freemarker.template;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.freemarker.template.model.User;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.beans.MapModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ExpressionLanguageTest {

  @Autowired
  Configuration freemarkerConfig;

  public static final User DEFAULT_USER = new User("default", "default@server.org");
  public static final List<User> ALL_USERS = List.of(DEFAULT_USER);

  @Test
  void test() throws IOException, TemplateException {
    assertThat(freemarkerConfig).isNotNull();

    Template template = freemarkerConfig.getTemplate("el.ftl");
    assertThat(template).isNotNull();

    List<User> users = IntStream.rangeClosed(1, 5)
        .mapToObj(i ->
            new User(
                String.format("username%s", i),
                String.format("email%s@server.org", i))
        )
        .collect(Collectors.toList());

    StringWriter outputWriter = new StringWriter();

    var data = Map.of("users", users);
    MapModel model = new MapModel(
        data,
        new BeansWrapperBuilder(Configuration.getVersion())
            .build());

    template.process(model, outputWriter);
    log.info("{}", outputWriter);
  }

}
