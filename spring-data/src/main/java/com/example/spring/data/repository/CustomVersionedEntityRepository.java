package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.VersionedEntity;

public interface CustomVersionedEntityRepository {

  VersionedEntity getAndLock(Long id);

}