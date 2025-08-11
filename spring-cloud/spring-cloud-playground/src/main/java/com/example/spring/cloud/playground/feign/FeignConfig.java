package com.example.spring.cloud.playground.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableFeignClients
@Import({FeignWebFluxConfig.class})
public class FeignConfig {

}
