package com.example.spring.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.jpa.model.VersionedEntity;
import com.example.spring.data.repository.OptimisticLockingTest.VersionedEntityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.diff.JsonDiff;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@DataJpaTest
@EnableRetry
@Import({VersionedEntityService.class, JacksonAutoConfiguration.class})
@Slf4j
class OptimisticLockingTest {

  @Service
  @RequiredArgsConstructor
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  static class VersionedEntityService {

    private final VersionedEntityRepository repository;
    private final ObjectMapper objectMapper;

    public long create() {
      var entity = new VersionedEntity();
      entity.setName("Entity 1");
      log.debug("{}", entity);
      repository.saveAndFlush(entity);
      log.debug("{}", entity);
      return entity.getId();
    }

    public VersionedEntity get(Long id) {
      return repository.findById(id).orElseThrow();
    }

    // NOTE: recover parameter can define the function in this class to recover
    @Retryable(maxAttempts = 3, backoff = @Backoff(multiplier = 3, random = true))
    public void updateValue(Long id, int value) {
      log.debug("updateEntity[{}]", value);
      var entity = repository.findById(id).orElseThrow();
      log.debug("updateEntity[{}] :: read entity", value);
      updateEntityData(entity, value);
      log.debug("updateEntity[{}] :: saving", value);
      repository.save(entity);
      log.debug("updateEntity[{}] :: saved :: {}", value, entity);
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(multiplier = 3, random = true))
    public void updateEntityPatchingUsingVersionCheck(VersionedEntity entity,
        VersionedEntity initialEntity) {
      var runId = "(t=%s)(v=%s)".formatted(Thread.currentThread().getName(), entity.getValue());
      log.debug("updateEntity[{}] :: {}", runId, entity);
      var currentEntity = repository.findById(entity.getId()).orElseThrow();
      log.debug("updateEntity[{}]  :: read current entity :: {}", runId, currentEntity);
      if (entity.getVersion() == currentEntity.getVersion()) {
        log.debug("Updating entity[{}]  :: no version conflict before saving", runId);
        repository.save(entity);
      } else {
        JsonPatch jsonPatch = getJsonDiff(initialEntity, entity);
        log.debug("Updating entity[{}]  :: with version conflict :: patch={}", runId, jsonPatch);
        VersionedEntity patchedEntity = applyJsonPatch(currentEntity, jsonPatch);
        repository.save(patchedEntity);
      }
    }

    public void updateEntityData(VersionedEntity entity, int value) {
      entity.setValue(value);
      Optional.of(value)
          .filter(v -> v % 2 == 1)
          .map("name-%s"::formatted)
          .ifPresent(entity::setName);
    }

    private <T> JsonPatch getJsonDiff(T source, T target) {
      var sourceJsonNode = objectMapper.convertValue(source, JsonNode.class);
      var targetJsonNode = objectMapper.convertValue(target, JsonNode.class);
      return JsonDiff.asJsonPatch(sourceJsonNode, targetJsonNode);
    }

    @SuppressWarnings("unchecked")
    private <T> T applyJsonPatch(T source, JsonPatch jsonPatch) {
      try {
        var sourceJsonNode = objectMapper.convertValue(source, JsonNode.class);
        var targetJsonNode = jsonPatch.apply(sourceJsonNode);
        var target = objectMapper.treeToValue(targetJsonNode, (Class<T>) source.getClass());
        return target;
      } catch (JsonPatchException | JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }

  }

  @Autowired
  VersionedEntityService service;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  void test_locking_simple() {
    var entityId = service.create();

    Flux.range(1, 10)
//        .log()
        .flatMap(v ->
                Mono.just(v)
//                .log()
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnNext(value -> {
                      service.updateValue(entityId, value);
                    })
        )
//        .log()
        .collectList()
        .block();

    var updatedEntity = service.get(entityId);
    log.debug("{}", updatedEntity);
  }

  @Test
  void test_locking_usingPatch() {
    var entityId = service.create();

    Flux.range(1, 10)
//        .log()
        .flatMap(v ->
                Mono.just(v)
//                .log()
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnNext(value -> {
                      VersionedEntity initialEntity = service.get(entityId);
                      VersionedEntity updatedEntity = clone(initialEntity);
                      assertThat(updatedEntity).isEqualTo(initialEntity);

                      service.updateEntityData(updatedEntity, value);
                      service.updateEntityPatchingUsingVersionCheck(updatedEntity, initialEntity);
                    })
        )
//        .log()
        .collectList()
        .block();

    var updatedEntity = service.get(entityId);
    log.debug("{}", updatedEntity);
  }

  private VersionedEntity clone(VersionedEntity entity) {
    try {
      byte[] bytes = objectMapper.writeValueAsBytes(entity);
      return objectMapper.readValue(bytes, VersionedEntity.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}