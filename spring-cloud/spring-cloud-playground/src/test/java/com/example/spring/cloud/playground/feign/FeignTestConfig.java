package com.example.spring.cloud.playground.feign;

import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;

@Import({JacksonAutoConfiguration.class, FeignAutoConfiguration.class, FeignClientsConfiguration.class})
public class FeignTestConfig {

}
