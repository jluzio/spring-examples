package com.example.spring.batch.playground.listener.log;

import static com.example.spring.batch.playground.listener.log.LogContextHelper.logContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class LogContextItemReaderListener extends LogContextStepExecutionListenerSupport implements
    ItemReadListener<Object> {

  @Override
  public void beforeRead() {
    logContext(log, stepExecution, "beforeRead");
  }

  @Override
  public void afterRead(Object item) {
    logContext(log, stepExecution, "afterRead");
  }

  @Override
  public void onReadError(Exception ex) {
    logContext(log, stepExecution, "onReadError");
  }
}
