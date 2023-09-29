package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.VersionedEntity;
import com.example.spring.data.repository.OptimisticLockingTest.VersionedEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
@Import({VersionedEntityService.class})
@Slf4j
class OptimisticLockingTest {

  @Service
  @RequiredArgsConstructor
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  static class VersionedEntityService {

    private final VersionedEntityRepository repository;

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

    @Retryable(maxAttempts = 3, backoff = @Backoff(multiplier = 3, random = true))
    public void update(Long id, int value) {
      repository.updateValue(id, value);
    }

  }

  @Autowired
  VersionedEntityService service;

  @Test
  void testOptimisticLocking() {
    var entityId = service.create();

    Flux.range(1, 3)
        .log()
        .flatMap(v ->
            Mono.just(v)
                .log()
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(value -> {
                  service.update(entityId, value);
                })
        )
        .log()
        .collectList()
        .block();

    var updatedEntity = service.get(entityId);
    log.debug("{}", updatedEntity);
  }

}