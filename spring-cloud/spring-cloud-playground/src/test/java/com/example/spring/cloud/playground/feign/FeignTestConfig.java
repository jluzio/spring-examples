package com.example.spring.cloud.playground.feign;

import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;

@Import({JacksonAutoConfiguration.class, FeignAutoConfiguration.class, FeignWebFluxConfig.class})
public class FeignTestConfig {

}
