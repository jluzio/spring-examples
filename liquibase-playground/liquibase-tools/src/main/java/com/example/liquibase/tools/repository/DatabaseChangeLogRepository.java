package com.example.liquibase.tools.repository;

import com.example.liquibase.tools.domain.Databasechangelog;
import com.example.liquibase.tools.domain.DatabasechangelogId;
import org.springframework.data.repository.CrudRepository;

public interface DatabaseChangeLogRepository extends CrudRepository<Databasechangelog, DatabasechangelogId> {

}
