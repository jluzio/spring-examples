package com.example.spring.batch.playground.features.remote_chunking.batch;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.dsl.Jms;

@Slf4j
@ConditionalOnProperty(RemoteChunkingBatchConfig.MANAGER_ENABLED_KEY)
public class ManagerConfig {

  /*
   * Configure outbound flow (requests going to workers)
   */
  @Bean
  public DirectChannel managerRequests() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow managerOutboundFlow(ActiveMQConnectionFactory connectionFactory, DirectChannel managerRequests) {
    return IntegrationFlow
        .from(managerRequests)
        .handle(Jms.outboundAdapter(connectionFactory).destination("requests"))
        .get();
  }

  /*
   * Configure inbound flow (replies coming from workers)
   */
  @Bean
  public QueueChannel managerReplies() {
    return new QueueChannel();
  }

  @Bean
  public IntegrationFlow managerInboundFlow(ActiveMQConnectionFactory managerConnectionFactory, QueueChannel managerReplies) {
    return IntegrationFlow
        .from(Jms.messageDrivenChannelAdapter(managerConnectionFactory).destination("replies"))
        .channel(managerReplies)
        .get();
  }

  @Bean
  public Step managerStep(
      RemoteChunkingManagerStepBuilderFactory managerStepBuilderFactory,
      ItemReader<String> managerItemReader,
      DirectChannel managerRequests,
      QueueChannel managerReplies
  ) {
    return managerStepBuilderFactory.get("managerStep")
        .chunk(10)
        .reader(managerItemReader)
        .outputChannel(managerRequests)
        .inputChannel(managerReplies)
        .build();
  }

  @Bean
  @StepScope
  public ItemReader<String> managerItemReader(@Value("#{jobParameters['data'].split(',')}") List<String> values) {
    return new ListItemReader<>(values);
  }

}
