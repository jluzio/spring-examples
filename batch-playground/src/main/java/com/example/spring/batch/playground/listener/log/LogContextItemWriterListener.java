package com.example.spring.batch.playground.listener.log;

import static com.example.spring.batch.playground.listener.log.LogContextHelper.logContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

@Slf4j
public class LogContextItemWriterListener
    extends LogContextStepExecutionListenerSupport
    implements ItemWriteListener<Object> {

  @Override
  public void beforeWrite(Chunk<?> items) {
    logContext(log, stepExecution, "beforeWrite");
  }

  @Override
  public void afterWrite(Chunk<?> items) {
    logContext(log, stepExecution, "afterWrite");
  }

  @Override
  public void onWriteError(Exception exception, Chunk<?> items) {
    logContext(log, stepExecution, "onWriteError");
  }
}
