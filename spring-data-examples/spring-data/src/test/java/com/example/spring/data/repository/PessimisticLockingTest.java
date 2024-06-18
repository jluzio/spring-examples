package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.VersionedEntity;
import com.example.spring.data.repository.PessimisticLockingTest.VersionedEntityService;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@DataJpaTest
@Import({VersionedEntityService.class, JacksonAutoConfiguration.class})
@Slf4j
class PessimisticLockingTest {

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

    public void updateEntityLockOnRead(Long id, int value) {
      log.debug("updateEntity[{}]", value);
      var entity = repository.getWithLock(id, LockModeType.PESSIMISTIC_WRITE);
      log.debug("updateEntity[{}] :: obtained lock", value);
      updateEntityData(entity, value);
      log.debug("updateEntity[{}] :: saving", value);
      repository.save(entity);
      log.debug("updateEntity[{}] :: saved :: {}", value, entity);
    }

    private void updateEntityData(VersionedEntity entity, int value) {
      entity.setValue(value);
      Optional.of(value)
          .filter(v -> v % 2 == 1)
          .map("name-%s"::formatted)
          .ifPresent(entity::setName);
    }

  }

  @Autowired
  VersionedEntityService service;

  @Test
  void test_locking_on_read() {
    var entityId = service.create();

    Flux.range(1, 10)
//        .log()
        .flatMap(v ->
            Mono.just(v)
//                .log()
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(value -> {
                  service.updateEntityLockOnRead(entityId, value);
                })
        )
//        .log()
        .collectList()
        .block();

    var updatedEntity = service.get(entityId);
    log.debug("{}", updatedEntity);
  }

}