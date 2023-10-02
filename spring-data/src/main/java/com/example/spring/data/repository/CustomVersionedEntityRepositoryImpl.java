package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.VersionedEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;


public class CustomVersionedEntityRepositoryImpl implements CustomVersionedEntityRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public VersionedEntity getAndLock(Long id) {
    return entityManager.find(
        VersionedEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
  }
}
