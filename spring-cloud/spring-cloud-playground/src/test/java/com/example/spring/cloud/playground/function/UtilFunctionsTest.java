package com.example.spring.cloud.playground.function;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.cloud.playground.function.model.User;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;

@FunctionalSpringBootTest
class UtilFunctionsTest {

  @Autowired
  private FunctionCatalog catalog;
  @Autowired
  private UserService service;


  @Test
  void users_logAndReturn() throws Exception {
    Supplier<Flux<User>> function = catalog.lookup("users,logAndReturn");
    assertThat(function.get().collectList().block())
        .isEqualTo(service.getUsers().collectList().block());
  }
}
