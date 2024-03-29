package com.example.spring.data.repository;

import com.example.spring.data.jpa.model.VersionedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VersionedEntityRepository extends JpaRepository<VersionedEntity, Long>,
    JpaSpecificationExecutor<VersionedEntity>, CustomVersionedEntityRepository {

}