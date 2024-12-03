package com.example.spring.observability;

import io.micrometer.tracing.Tracer;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TraceContextAwareExecutor implements Executor {

  private final Executor delegate;
  private final Tracer tracer;

  @Override
  public void execute(Runnable command) {
    tracer.currentTraceContext().wrap(delegate).execute(command);
  }
}
