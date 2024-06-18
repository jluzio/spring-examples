package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.VersionedEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CustomVersionedEntityRepositoryImpl implements CustomVersionedEntityRepository {

  @PersistenceContext
  private final EntityManager entityManager;

  @Override
  public VersionedEntity getWithLock(Long id, LockModeType lockMode) {
    return entityManager.find(
        VersionedEntity.class, id, lockMode);
  }

  @Override
  public void lock(VersionedEntity entity, LockModeType lockMode) {
    entityManager.lock(entity, lockMode);
  }
  @Override
  public void refresh(VersionedEntity entity, LockModeType lockMode) {
    entityManager.refresh(entity, lockMode);
  }

}
