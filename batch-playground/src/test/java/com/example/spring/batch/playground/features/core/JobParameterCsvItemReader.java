package com.example.spring.batch.playground.features.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

@RequiredArgsConstructor
@Slf4j
public class JobParameterCsvItemReader implements ItemReader<String>, StepExecutionListener {

  private final String propertyName;
  private List<String> items;
  private Iterator<String> itemsIterator;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    String valuesCsv = Objects.requireNonNull(stepExecution.getJobParameters().getString(propertyName));
    this.items = Arrays.asList(valuesCsv.split(","));
    this.itemsIterator = items.iterator();
    log.info("{} :: items={}", getClass().getSimpleName(), items);
  }

  @Override
  public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    return itemsIterator.hasNext()
        ? itemsIterator.next()
        : null;
  }

}
