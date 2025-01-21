package com.example.spring.core.aop.spring.shorthand_refs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.LoggingAspectService;
import com.example.spring.core.aop.spring.shorthand_refs.ShorthandPointcutAspectTest.Config.ShorthandCommonPointcutAspect;
import com.example.spring.core.aop.spring.shorthand_refs.ShorthandPointcutAspectTest.Config.SimpleService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
class ShorthandPointcutAspectTest {

  @Configuration
  @Import({AopAutoConfiguration.class, ShorthandCommonPointcuts.class,
      LoggingAspectService.class})
  static class Config {

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
        return service.logProfiling(joinPoint, this);
      }
    }
  }

  @Autowired
  private SimpleService simpleService;
  @MockitoSpyBean
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
