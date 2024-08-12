package com.example.spring.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.jpa.model.VersionedEntity;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({EntityManagerCustomRepository.class, JacksonAutoConfiguration.class})
@Slf4j
class EntityManagerCustomRepositoryTest {

  @Autowired
  EntityManagerCustomRepository repository;

  @Test
  void test() {
    VersionedEntity entity = new VersionedEntity();
    entity.setName("ent-1");
    entity.setVersion(1);
    repository.persist(entity);

    assertThat(entity.getId())
        .isNotNull();

    var loadedEntity = repository.findUsingLockMode(
        VersionedEntity.class,
        entity.getId(),
        LockModeType.PESSIMISTIC_WRITE);
    log.debug("{}", loadedEntity);
  }

}