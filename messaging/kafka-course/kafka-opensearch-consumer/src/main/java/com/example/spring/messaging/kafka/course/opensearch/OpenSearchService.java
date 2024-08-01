package com.example.spring.messaging.kafka.course.opensearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.FlatObjectProperty;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class OpenSearchService {

  private final OpenSearchConfigProps configProps;
  private final OpenSearchClient client;
  private final ObjectMapper objectMapper;

  record IndexEventData(String id, Map<String, Object> data) {

  }

  public void createIndexIfRequired() throws IOException {
    var existsRequest = new ExistsRequest.Builder()
        .index(configProps.getIndex())
        .build();
    var existsResponse = client.indices().exists(existsRequest);

    log.info("OpenSearch Index {} :: exists={}",
        configProps.getIndex(), existsResponse.value());
    if (!existsResponse.value()) {
      var createIndexRequest = new CreateIndexRequest.Builder()
          .index(configProps.getIndex())
          .mappings(
              mappings -> mappings.properties(
                  "data",
                  props -> props.flatObject(new FlatObjectProperty.Builder().build()))
          )
          .build();
      var createIndexResponse = client.indices()
          .create(createIndexRequest);
      log.info("OpenSearch Index {} :: created :: {}",
          configProps.getIndex(), createIndexResponse);
    }
  }

  @SuppressWarnings("unchecked")
  public void index(ConsumerRecord<String, String> consumerRecord) throws IOException {
    Map<String, Object> eventData = objectMapper.readValue(consumerRecord.value(), Map.class);
    var eventId = Optional.ofNullable(eventData.get("id"))
        .map(Objects::toString)
        .orElse(null);
    String indexId = Optional.ofNullable(eventId)
        .orElseGet(() -> UUID.randomUUID().toString());

    var indexRequest = new IndexRequest.Builder<IndexEventData>()
        .id(indexId)
        .index(configProps.getIndex())
        .document(new IndexEventData(eventId, eventData))
        .build();
    var response = client.index(indexRequest);
    log.info("Created OpenSearch index: {} | {}", response.id(), response.index());
  }
}
