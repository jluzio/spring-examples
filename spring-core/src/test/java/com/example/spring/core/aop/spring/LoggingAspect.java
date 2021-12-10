package com.example.spring.core.aop.spring;

import static java.util.Optional.ofNullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingAspect {

  public Object logInvocation(ProceedingJoinPoint joinPoint, Object aspectCaller) throws Throwable {
    var start = LocalDateTime.now();
    Throwable throwable = null;
    Object output = null;

    try {
      output = joinPoint.proceed();
    } catch (Throwable e) {
      throwable = e;
    }

    var invocationDuration = Duration.between(start, LocalDateTime.now());
    log.info("{} :: {} executed in {}ms",
        aspectCaller.getClass().getSimpleName(),
        joinPoint.getSignature(),
        invocationDuration.toMillis());
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
