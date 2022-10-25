package com.example.spring.core.aop.spring;

import com.example.spring.core.aop.spring.service.AnotherService;
import com.example.spring.core.aop.spring.service.SomeService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    SomeService.class,
    AnotherService.class,
    LoggingAspectService.class
})
public class ServicesConfig {

}
