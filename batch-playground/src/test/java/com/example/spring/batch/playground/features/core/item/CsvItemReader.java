package com.example.spring.batch.playground.features.core.item;

import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

@RequiredArgsConstructor
@Slf4j
public class CsvItemReader implements ItemReader<String> {

  private final ThreadLocal<Iterator<String>> itemsIteratorThreadLocal = new ThreadLocal<>();
  private final String propertyName;

  @Override
  public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    var iterator = itemsIteratorThreadLocal.get();
    return iterator.hasNext()
        ? iterator.next()
        : null;
  }

}
