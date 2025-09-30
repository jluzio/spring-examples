package com.example.spring.batch.playground.features.remote_chunking.batch;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.dsl.Jms;

@Slf4j
@ConditionalOnProperty(RemoteChunkingBatchConfig.WORKER_ENABLED_KEY)
public class WorkerConfig {

  /*
   * Configure inbound flow (requests coming from the manager)
   */
  @Bean
  public DirectChannel workerRequests() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow workerInboundFlow(ActiveMQConnectionFactory connectionFactory, DirectChannel workerRequests) {
    return IntegrationFlow
        .from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("requests"))
        .channel(workerRequests)
        .get();
  }

  /*
   * Configure outbound flow (replies going to the manager)
   */
  @Bean
  public DirectChannel workerReplies() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow workerOutboundFlow(ActiveMQConnectionFactory workerConnectionFactory, DirectChannel workerReplies) {
    return IntegrationFlow
        .from(workerReplies)
        .handle(Jms.outboundAdapter(workerConnectionFactory).destination("replies"))
        .get();
  }

  // worker step
  @Bean
  public IntegrationFlow workerStep(
      RemoteChunkingWorkerBuilder<String, String> workerBuilder,
      DirectChannel workerRequests,
      DirectChannel workerReplies,
      ItemProcessor<String, String> itemProcessor,
      ItemWriter<String> itemWriter
  ) {
    return workerBuilder
        .itemProcessor(itemProcessor)
        .itemWriter(itemWriter)
        .inputChannel(workerRequests)
        .outputChannel(workerReplies)
        .build();
  }

  @Bean
  ItemProcessor<String, String> workerItemProcessor() {
    return item -> {
      log.info("itemProcessor :: {}", item);
      return item;
    };
  }

  @Bean
  ItemWriter<String> workerItemWriter() {
    return item -> {
      log.info("itemWriter :: {}", item);
    };
  }

}
