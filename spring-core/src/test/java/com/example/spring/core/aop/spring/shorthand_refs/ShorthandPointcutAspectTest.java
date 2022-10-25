package com.example.spring.core.aop.spring.shorthand_refs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.LoggingAspectService;
import com.example.spring.core.aop.spring.shorthand_refs.ShorthandPointcutAspectTest.ShorthandCommonPointcutAspect;
import com.example.spring.core.aop.spring.shorthand_refs.ShorthandPointcutAspectTest.SimpleService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
@EnableAspectJAutoProxy
@Import({SimpleService.class, ShorthandCommonPointcuts.class, ShorthandCommonPointcutAspect.class})
class ShorthandPointcutAspectTest {

  @Component
  public static class SimpleService {

    @LogStuff
    public void run() {
      log.info("SimpleService :: run");
    }
  }

  @Component
  @Aspect
  public static class ShorthandCommonPointcutAspect {

    @Autowired
    private LoggingAspectService service;

    // Omitted package, since it's visible in the same package.
    @Around("ShorthandCommonPointcuts.logStuffAnnotation()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
      return service.logTimeElapsed(joinPoint, this);
    }
  }

  @Autowired
  private SimpleService simpleService;
  @SpyBean
  private ShorthandCommonPointcutAspect commonPointcutAspect;

  @Test
  void test() throws Throwable {
    clearMockInvocations();
    simpleService.run();
    verify(commonPointcutAspect, times(1))
        .handle(any());
  }

  private void clearMockInvocations() {
    clearInvocations(
        commonPointcutAspect);
  }

}
