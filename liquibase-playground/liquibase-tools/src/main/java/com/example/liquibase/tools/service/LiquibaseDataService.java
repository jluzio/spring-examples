package com.example.liquibase.tools.service;

import com.example.liquibase.tools.repository.DatabaseChangeLogRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LiquibaseDataService {

  private final DatabaseChangeLogRepository changeLogRepository;

  public void clearChangeLog() {
    changeLogRepository.deleteAll();
  }

}
