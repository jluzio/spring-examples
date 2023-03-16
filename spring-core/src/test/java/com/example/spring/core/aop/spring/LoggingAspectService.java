package com.example.spring.core.aop.spring;

import static java.util.Optional.ofNullable;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingAspectService {

  public Object logProfiling(ProceedingJoinPoint joinPoint, Object aspectCaller) throws Throwable {
    var stopwatch = Stopwatch.createStarted();
    Throwable throwable = null;
    Object output = null;

    try {
      output = joinPoint.proceed();
    } catch (Throwable e) {
      throwable = e;
    }

    stopwatch.stop();
    log.info("{} :: {} executed in {}",
        aspectCaller.getClass().getSimpleName(),
        joinPoint.getSignature(),
        stopwatch);
    log.info("{} :: {} :: input={} | output={} | throwable={}",
        aspectCaller.getClass().getSimpleName(),
        joinPoint.getSignature(),
        joinPoint.getArgs(),
        output,
        ofNullable(throwable).map(Throwable::toString).orElse(null));

    if (throwable != null) {
      throw throwable;
    }
    return output;
  }

  public Object logInvocation(ProceedingJoinPoint joinPoint, Object aspectCaller) throws Throwable {
    log.info("{} :: {} :: input={}",
        aspectCaller.getClass().getSimpleName(),
        joinPoint.getSignature(),
        joinPoint.getArgs());

    Throwable throwable = null;
    Object output = null;
    try {
      output = joinPoint.proceed();
    } catch (Throwable e) {
      throwable = e;
    }

    log.info("{} :: {} :: input={} | output={} | throwable={}",
        aspectCaller.getClass().getSimpleName(),
        joinPoint.getSignature(),
        joinPoint.getArgs(),
        output,
        ofNullable(throwable).map(Throwable::toString).orElse(null));

    if (throwable != null) {
      throw throwable;
    }
    return output;
  }
}
