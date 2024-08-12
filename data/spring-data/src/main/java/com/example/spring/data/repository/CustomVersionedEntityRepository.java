package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.VersionedEntity;
import jakarta.persistence.LockModeType;

public interface CustomVersionedEntityRepository {

  VersionedEntity getWithLock(Long id, LockModeType lockMode);

  void lock(VersionedEntity entity, LockModeType lockMode);

  void refresh(VersionedEntity entity, LockModeType lockMode);
}