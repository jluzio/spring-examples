package com.example.spring.batch.playground.listener.log;

import static com.example.spring.batch.playground.listener.log.LogContextHelper.logContext;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;

@Slf4j
public class LogContextItemWriterListener extends LogContextStepExecutionListenerSupport implements
    ItemWriteListener<Object> {

  @Override
  public void beforeWrite(List<?> items) {
    logContext(log, stepExecution, "beforeWrite");
  }

  @Override
  public void afterWrite(List<?> items) {
    logContext(log, stepExecution, "afterWrite");
  }

  @Override
  public void onWriteError(Exception exception, List<?> items) {
    logContext(log, stepExecution, "onWriteError");
  }
}
