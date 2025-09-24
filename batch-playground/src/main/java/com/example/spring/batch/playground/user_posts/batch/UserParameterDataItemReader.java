package com.example.spring.batch.playground.user_posts.batch;

import com.example.spring.batch.playground.user_posts.persistence.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserParameterDataItemReader implements ItemReader<User>, StepExecutionListener {

  private final ObjectMapper objectMapper;
  private List<User> values;
  private Iterator<User> valuesIterator;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    try {
      var inputUsersJson = stepExecution.getJobParameters().getString(JobParameters.INPUT_USERS);
      values = Arrays.stream(objectMapper.readValue(inputUsersJson, User[].class)).toList();
      valuesIterator = values.iterator();
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public User read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    return valuesIterator.hasNext()
        ? valuesIterator.next()
        : null;
  }

}
