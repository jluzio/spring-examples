package com.example.spring.batch.playground.features.resilience.persistence.service;

import com.example.spring.batch.playground.features.resilience.persistence.model.JobData;
import com.example.spring.batch.playground.features.resilience.persistence.repository.JobDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobDataService {

  private final JobDataRepository repository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateJobData(String id, String data) {
    var jobData = JobData.builder()
        .id(id)
        .data(data)
        .build();
    repository.save(jobData);
  }

}
