package com.example.spring.data.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.spring.data.jpa.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Flux;

@DataJpaTest
@Slf4j
class OptimisticLockingTest {

  @Autowired
  private EmployeeRepository repository;

  @Test
  void testOptimisticLocking() {
    // Create and save the entity
    var entity = new Employee();
    entity.setName("Entity 1");
    log.debug("{}", entity);

    repository.save(entity);
    log.debug("{}", entity);

//    Flux.range(1, 10)
//        .

    // Retrieve the entity from the repository
    var retrievedEntity = repository.findById(entity.getId()).orElse(null);
    assert retrievedEntity != null;
    log.debug("{}", entity);

    // Modify the entity
    retrievedEntity.setName("Modified Entity");

    // Save the entity to simulate a concurrent update
    repository.save(retrievedEntity);
    log.debug("{}", entity);

    // Try to save the entity again, which should result in an OptimisticLockingFailureException
    assertThrows(OptimisticLockingFailureException.class, () -> repository.save(entity));
  }
}