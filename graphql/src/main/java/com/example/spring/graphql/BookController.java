package com.example.spring.graphql;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class BookController {

  @QueryMapping
  public List<Book> books() {
    log.debug("books");
    return Book.findAll();
  }

  @QueryMapping
  public Book bookById(@Argument String id) {
    log.debug("bookById :: {}", id);
    return Book.getById(id);
  }

  /**
   * Mapping of Author from Book, this one is using batch
   */
  @BatchMapping
  public Map<Book, Author> author(List<Book> books) {
    log.debug("authorsByBookMap :: {}", books);
    return books.stream()
        .collect(Collectors.toMap(Function.identity(), this::author));
  }

  /**
   * Mapping of Author from Book, this one is one per book.
   * Commented so it uses the batch one.
   */
//  @SchemaMapping
  public Author author(Book book) {
    log.debug("author :: {}", book);
    return Author.getById(book.authorId());
  }
}