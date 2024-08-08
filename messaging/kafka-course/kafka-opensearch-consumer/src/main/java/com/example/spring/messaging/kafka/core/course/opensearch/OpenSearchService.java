package com.example.spring.messaging.kafka.core.course.opensearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.FlatObjectProperty;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.bulk.BulkResponseItem;
import org.opensearch.client.opensearch.core.bulk.IndexOperation;
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

  record IndexEventData(String id, Object data) {

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

  public void index(ConsumerRecord<String, String> consumerRecord) throws IOException {
    var indexRequest = getIndexRequest(consumerRecord);
    var response = client.index(indexRequest);
    log.info("Created OpenSearch index: {} | {}", response.id(), response.index());
  }

  private IndexRequest<IndexEventData> getIndexRequest(
      ConsumerRecord<String, String> consumerRecord) {
    try {
      JsonNode eventDataJsonNode = objectMapper.readTree(consumerRecord.value());
      // Map<String, Object> eventData = objectMapper.treeToValue(eventDataJsonNode, Map.class);
      var eventId = getEventDataId(eventDataJsonNode);

      return new IndexRequest.Builder<IndexEventData>()
          .id(eventId)
          .index(configProps.getIndex())
          .document(new IndexEventData(eventId, eventDataJsonNode))
          .build();
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Using ID that identifies the message to make the Consumer idempotent and don't create
   * duplicates in OpenSearch. Strategies: - Message ID: Topic + Partition + Offset - Data ID: in
   * this case "$.meta.id"
   */
  private String getEventDataId(JsonNode eventDataJsonNode) {
    return eventDataJsonNode.at("/meta/id").asText();
  }

  public void bulkIndex(ConsumerRecords<String, String> consumerRecords) throws IOException {

    var bulkOperations = StreamSupport.stream(consumerRecords.spliterator(), false)
        .map(consumerRecord -> {
          IndexRequest<IndexEventData> indexRequest = getIndexRequest(consumerRecord);
          return new BulkOperation.Builder()
              .index(new IndexOperation.Builder<>()
                  .id(indexRequest.id())
                  .index(indexRequest.index())
                  .document(indexRequest.document())
                  .build())
              .build();
        })
        .toList();

    var indexRequest = new BulkRequest.Builder()
        .operations(bulkOperations)
        .build();
    var response = client.bulk(indexRequest);

    var ingestedIds = response.items().stream()
        .map(BulkResponseItem::id)
        .toList();
    var ingestedIndexes = response.items().stream()
        .map(BulkResponseItem::index)
        .collect(Collectors.toSet());
    log.info("Created OpenSearch index: {} | {}", ingestedIds, ingestedIndexes);
  }
}
