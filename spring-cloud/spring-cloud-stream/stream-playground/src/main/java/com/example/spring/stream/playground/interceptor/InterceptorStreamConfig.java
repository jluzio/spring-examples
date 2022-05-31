package com.example.spring.stream.playground.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

@Configuration
@Profile("interceptor")
@Slf4j
public class InterceptorStreamConfig {

  @Bean
  @GlobalChannelInterceptor(patterns = "*")
  public ChannelInterceptor globalInterceptor() {
    return new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.trace("Intercepted :: message={} | channel={}", message, channel);
        return message;
      }
    };
  }

  @Bean
  @GlobalChannelInterceptor(patterns = "ping")
  public ChannelInterceptor pingInterceptor() {
    return new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.debug("Intercepted ping {} in channel {}", new String((byte[]) message.getPayload()),
            channel);
        return message;
      }
    };
  }
}
