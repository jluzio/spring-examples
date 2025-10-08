package com.example.liquibase.tools.service;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.resource.ResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

@RequiredArgsConstructor
@Slf4j
public class LiquibaseFactory {

  private final Database database;
  private final ResourceAccessor resourceAccessor;


  @Cacheable("liquibase")
  public Liquibase get(String changeLog) {
    Liquibase liquibase = new Liquibase(changeLog, resourceAccessor, database);
    log.info("Creating Liquibase for changeLog={} and connectionURL={}",
        changeLog, liquibase.getDatabase().getConnection().getURL());
    return liquibase;
  }

}
