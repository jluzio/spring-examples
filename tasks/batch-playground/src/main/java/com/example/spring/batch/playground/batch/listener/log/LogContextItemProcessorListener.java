package com.example.spring.batch.playground.batch.listener.log;

import static com.example.spring.batch.playground.batch.listener.log.LogContextHelper.logContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;

@Slf4j
public class LogContextItemProcessorListener extends LogContextStepExecutionListenerSupport
    implements ItemProcessListener<Object, Object> {

  @Override
  public void beforeProcess(Object item) {
    logContext(log, stepExecution, "beforeProcess");
  }

  @Override
  public void afterProcess(Object item, Object result) {
    logContext(log, stepExecution, "afterProcess");
  }

  @Override
  public void onProcessError(Object item, Exception e) {
    logContext(log, stepExecution, "onProcessError");
  }
}
