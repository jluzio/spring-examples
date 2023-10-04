package com.example.spring.data.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EntityManagerCustomRepository {

  @PersistenceContext
  private final EntityManager entityManager;

  public <T> void persist(T entity) {
    entityManager.persist(entity);
  }

  public <T, ID> T findUsingLockMode(Class<T> clazz, ID id, LockModeType lockMode) {
    return entityManager.find(clazz, id, lockMode);
  }

  public <T> void lock(T entity, LockModeType lockMode) {
    entityManager.lock(entity, lockMode);
  }

}
